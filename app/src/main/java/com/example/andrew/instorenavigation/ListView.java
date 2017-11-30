/********************************************************************************************
 LIST VIEW IS THE FIRST SCREEN THAT A USER WILL COME TO AFTER LOGGING IN. FROM THIS SCREEN
 THE USER CAN ADD A NEW LIST, DELETE A LIST, GET TO EDIT LIST USING THE EDIT BUTTON, AND
 NAVIGATE A LIST BY USING THE NAVIGATION BUTTON.

 NEED TO GO IN AND HAVE AN OPTION FOR LISTS THAT DON'T MATCH A STORE
 *******************************************************************************************/
package com.example.andrew.instorenavigation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import android.widget.ListView;


public class ListView extends AppCompatActivity {
    //Objects to generate a field for each list.
    ArrayAdapter<String> mAdapter;
    android.widget.ListView lstNames;

    //Variables passed forward from Login Activity
    private String userID;
    private Context context;

    //Variable to store the List Name entered
    TextView listName;

    //Container for lists.
    private ArrayList<String> lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        context = this;

        //Instantiate list container
        lists = new ArrayList<>();

        //Grab area where to place lists.
        lstNames = findViewById(R.id.lists);

        listName = (TextView) findViewById(R.id.txtListName);

        //Store User_ID passed forward form login.
        Intent loginID = getIntent();
        userID = loginID.getStringExtra("userID");

        //Set title of view.
        super.setTitle("Your Lists");

        //Query for each of the listS associated with the user.
        queryLists(userID, this);
    }

    //Use the adapter to create an entry for each list.
    private void loadTaskList() {
        try {
            mAdapter = new ArrayAdapter<String>(this, R.layout.generate_list_view, R.id.list_title, lists);
            lstNames.setAdapter(mAdapter);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        //Change menu icon color
        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);

        return super.onCreateOptionsMenu(menu);
    }

    @Override // handle the user clicking the settings button
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId()) //get the id which is an int
        {
            case R.id.settings_action:
                Intent i = new Intent(this, SettingsView.class);
                i.putExtra("UserID", userID);
                startActivity(i);
                break;

            default:

        }
        return true;
    }


    //Prompt used by delete button to ensure that the user wishes to remove the list.
    public void deleteTask(final View view){ //Method used to delete the generate_list_view selected
        AlertDialog dialog = new AlertDialog.Builder(this) //Create prompt to ask if user wants to delete a itemlist
                .setMessage("Do you want to delete this itemlist?") //Prompt message for user
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        View parent = (View)view.getParent();
                        TextView taskTextView = (TextView)parent.findViewById(R.id.list_title);
                        Log.e("String", (String) taskTextView.getText());
                        String task = String.valueOf(taskTextView.getText());

                        deleteList(userID, task, context);
                    }
                })
                .setNegativeButton("Cancel",null)
                .create();
        dialog.show();
    }

    //Method called on click of edit button.
    public void editList(View view) {
        View parent = (View)view.getParent();
        TextView taskTextView = (TextView)parent.findViewById(R.id.list_title);
        Log.e("String", (String) taskTextView.getText());
        String task = String.valueOf(taskTextView.getText());

        //Pass forward the user ID and the name of the list being edited.
        Intent i = new Intent(this, EditListView.class);
        i.putExtra("ListName", task);
        i.putExtra("UserID", userID);
        startActivity(i);
    }

    //Method called on navigation click.
    public void storeSelect(View view) {
        View parent = (View)view.getParent();
        TextView taskTextView = (TextView)parent.findViewById(R.id.list_title);
        Log.e("String", (String) taskTextView.getText());
        String task = String.valueOf(taskTextView.getText());

        //Will take the current list and query for the stores that hold all of the
            //items.
        queryItems(task);
    }

    //Query to insert the new list into the database.
    public void addList(final String key1, final String key2, Context context) {
        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(context);
        String responseValue = null;


        String url = "http://34.238.160.248/InsertList.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        //Check to make sure that the list doesn't exist
                        if(response.equals("List Already Exists.")){
                            Context appContext = getApplicationContext();
                            CharSequence text = "List " + key1 + " already exists.";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();
                        //Unexpected error
                        } else if (response.equals("Bad")){
                            Context appContext = getApplicationContext();
                            CharSequence text = "An Unexpected Error Occurred.";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();
                        //List created Successfully.
                        } else {
                            lists.add(key1);

                            Context appContext = getApplicationContext();
                            CharSequence text = "List " + key1 + " successfully created.";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();

                            //Reload lists when creation is done.
                            loadTaskList();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Name", key1);
                params.put("Users_UserID", key2);

                return params;
            }
        };
        queue.add(postRequest);
    }

    //Query to remove a list from a database.
    public void deleteList(final String Users_UserID, final String Name, Context context) {
        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(context);
        String responseValue = null;


        String url = "http://34.238.160.248/DeleteList.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(!response.equals("Bad")){
                            lists.remove(Name);

                            Context appContext = getApplicationContext();
                            CharSequence text = "List " + Name + " successfully deleted.";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();

                            //Reload lists when list is deleted.
                            loadTaskList();
                        } else {
                            Context appContext = getApplicationContext();
                            CharSequence text = "An Unexpected Error Occurred";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Users_UserID", Users_UserID);
                params.put("Name", Name);

                return params;
            }
        };


        queue.add(postRequest);
    }

    public void listNameEnter(final View view){

        String task = String.valueOf(listName.getText());
        addList(task, userID, context);

        listName.setText("");
    }

    //Query to get all lists associated with a user.
    private void queryLists(final String key1, Context context) {
        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(context);
        String responseValue = null;


        String url = "http://34.238.160.248/GetList.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.length() >= 1){
                            parseListNames(response);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Users_UserID", key1);
                params.put("Users_UserID", key1);

                return params;
            }
        };
        queue.add(postRequest);
    }

    //Query to get all items associated with a list.
    private void queryItems(final String listName) {
        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(context);
        String responseValue = null;


        String url = "http://34.238.160.248/GetListContent.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.equals("Bad")){
                            Context appContext = getApplicationContext();
                            CharSequence text = "Cannot Navigate Empty List";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();
                        } else {
                            goToStoreView(response, listName);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("List_Name", listName);
                params.put("Users_UserID", userID);
                //  params.put("Product_Name", items.);

                return params;
            }
        };
        queue.add(postRequest);
    }

    //Parse list names from query response.
    private void parseListNames(String lists) {
        int i = 0;

        for (int j = 0; j < lists.length(); j++) {
            String check = lists.substring(j,j+1);
            if (check.equals("`")) {
                String temp = lists.substring(i,j);
                this.lists.add(temp);
                i = j+1;
            }
        }

        loadTaskList();
    }

    //Method called after stores have been found for a list.
    private void goToStoreView(String productString, String listName) {
        Intent i = new Intent(this, StoreView.class);
        i.putExtra("ProductsString", productString);
        i.putExtra("UserID", userID);
        i.putExtra("ListName", listName);
        startActivity(i);
    }


}