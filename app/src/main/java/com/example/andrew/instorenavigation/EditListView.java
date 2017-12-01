/************************************************************************************************
 EDIT LIST VIEW WILL ALLOW THE USER TO ADD AND DELETE ITEMS INSIDE OF A SELECTED LIST. THE NAME
 OF THE LIST WILL BE DISPLAYED AS TEH TITLE OF THE VIEW. A USER MUST ENTER THE PRODUCT NAME VIA
 TEXT FIELD. IT CURRENTLY ALLOWS FOR ANY INPUT AND HAS NO AUTOCOMPLETE, BUT THE ADDITION WILL ONLY
 OCCUR IF A PRODUCT IS WITHIN THE DATABASE.
 ***********************************************************************************************/
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
import android.widget.AutoCompleteTextView;
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

public class EditListView extends AppCompatActivity {

    //Adapter is used to display every item contained within a list.
    ArrayAdapter<String> mAdapter;
    android.widget.ListView lstTask;

    //Variables passed forward from previous view.
    private String userID, listName;

    private Context context;
    //Arraylist to hold all items inside of the list.
    private ArrayList<String> items;
    private ArrayList<String> itemsForAutoComplete;

    private TextView itemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list_view);
        context = this;

        lstTask = findViewById(R.id.edit_list);
        Intent loginID = getIntent();
        itemName = findViewById(R.id.txtItem);

        //Pull the passed forward data fro List View.
        userID = loginID.getStringExtra("UserID");
        listName = loginID.getStringExtra("ListName");

        //Set title to the list being edited.
        super.setTitle(listName);

        //Instantiate items array.
        items = new ArrayList<>();
        itemsForAutoComplete = new ArrayList<>();
        queryForItems();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,itemsForAutoComplete);
        AutoCompleteTextView textView = findViewById(R.id.txtItem);
        textView.setAdapter(adapter);

        queryItems();
    }

    // ---------- Load Task List ----------
    private void loadTaskList() {
        if(mAdapter==null){
            mAdapter = new ArrayAdapter<String>(this,R.layout.generate_edit_list_view,R.id.item_title,items);
            lstTask.setAdapter(mAdapter);//Populates the contents of the EditListView
        }
        else{
            mAdapter = new ArrayAdapter<String>(this,R.layout.generate_edit_list_view,R.id.item_title,items);
            lstTask.setAdapter(mAdapter);//Populates the contents of the EditListView
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    // ---------- Creates the add button  ----------
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

    // ---------- Method used to delete the items from the list ----------
    public void deleteTask(final View view){ //Method used to delete the generate_list_view selected
        AlertDialog dialog = new AlertDialog.Builder(this) //Create prompt to ask if user wants to delete a itemlist
                .setMessage("Do you want to delete this itemlist?") //Prompt message for user
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        View parent = (View)view.getParent();
                        TextView taskTextView = parent.findViewById(R.id.item_title);
                        Log.e("String", (String) taskTextView.getText());
                        String task = String.valueOf(taskTextView.getText());
                        deleteItem(listName, userID, task, context);
                        loadTaskList();
                    }
                })
                .setNegativeButton("Cancel",null)
                .create();
        dialog.show();


    }

    // ---------- Enter Items ----------
    public void enterItems(final View view){
        String task = String.valueOf(itemName.getText());
        if(items.contains(task)){
            Context appContext = getApplicationContext();
            CharSequence text = "Item Already On List.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(appContext, text, duration);
            toast.show();
        } else {
            testItem(task);
        }


    }

    // ---------- Add Items ----------
    public void addItems(final String key1, final String key2, final String key3, Context context) {
        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(context);
        String responseValue = null;


        String url = "http://34.238.160.248/InsertListProd.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.length() > 1){
                            items.add(key3);
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
                params.put("List_Name", key1);
                params.put("Users_UserID", key2);
                params.put("Product_Name", key3);

                return params;
            }
        };
        queue.add(postRequest);
    }

    // ---------- Delete Item ----------
    public void deleteItem(final String ListName,final String userID, final String item, Context context) {
        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(context);
        String responseValue = null;


        String url = "http://34.238.160.248/DeleteContent.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if (response.equals("Bad")) {
                            Context appContext = getApplicationContext();
                            CharSequence text = "An Unexpected Error Occurred";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();
                        } else {
                            Log.d("Response", response);
                            Context appContext = getApplicationContext();
                            CharSequence text = item + " removed from list.";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();
                            items.remove(item);
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
                params.put("Users_UserID", userID);
                params.put("List_Name", ListName);
                params.put("Product_Name", item);


                return params;
            }
        };


        queue.add(postRequest);
    }

    // ---------- Query Items ----------
    private void queryItems() {
        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(context);
        final String responseValue = null;


        String url = "http://34.238.160.248/GetListContent.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.length() >= 1){
                            parseItemNames(response, items);
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

                return params;
            }
        };
        queue.add(postRequest);
    }

    // ---------- Parse Item Names ----------
    private void parseItemNames(String items, ArrayList list) {
        int i = 0;

        for (int j = 0; j < items.length(); j++) {
            String check = items.substring(j,j+1);
            if (check.equals("`")) {
                String temp = items.substring(i,j);
                list.add(temp);
                i = j+1;
            }
        }

        loadTaskList();
    }

    // ---------- Test to see if item is in the database ----------
    private void testItem (final String item){

        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(context);
        String responseValue = null;

        String url = "http://34.238.160.248/CheckProduct.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if (response.equals(item)) {
                            addItems(listName, userID, item, context);//Adds item to the list if in the database
                            Context appContext = getApplicationContext();
                            CharSequence text = item + " added to list.";
                            int duration = Toast.LENGTH_SHORT;

                            itemName.setText("");
                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();
                            loadTaskList();
                        } else {
                            Context appContext = getApplicationContext();
                            CharSequence text = "Item not Found";
                            int duration = Toast.LENGTH_SHORT;

                            itemName.setText("");
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
                params.put("Product_Name", item);
                return params;
            }
        };

        queue.add(postRequest);
    }

    //When the done button is clicked, will finish EditList Activity and
        //redirect user back to ListView
    public void back(final View v) {
        finish();
    }

    //Query for the items to be autofilled
    public void queryForItems(){
        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(context);
        final String responseValue = null;


        String url = "http://34.238.160.248/GetProduct.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.length() >= 1){
                            parseItemNames(response, itemsForAutoComplete);
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


                return params;
            }
        };
        queue.add(postRequest);
    }
}