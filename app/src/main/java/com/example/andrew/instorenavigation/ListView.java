//***********************************************
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
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * This code was created with the help of
 * https://www.youtube.com/watch?v=RXtj4TxMmW0
 */

public class ListView extends AppCompatActivity {

    ArrayAdapter<String> mAdapter;
    android.widget.ListView lstNames;

    private String userID;
    private Context context;
    private String LID;
    private ArrayList<String> lists;

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle("Your Lists");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        context = this;
        lists = new ArrayList<>();

        lstNames = findViewById(R.id.lists);
        Intent loginID = getIntent();
        userID = loginID.getStringExtra("userID");

        queryLists(userID, this);
    }

    private void loadTaskList() {

        try {
            if (mAdapter == null) {
               mAdapter = new ArrayAdapter<String>(this, R.layout.generate_list_view, R.id.list_title, lists);
               lstNames.setAdapter(mAdapter);
            } else {
                mAdapter = new ArrayAdapter<String>(this, R.layout.generate_list_view, R.id.list_title, lists);
                lstNames.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_task:
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this) //Create prompt to ask if user wants to create a new itemlist
                        .setTitle("Create New List")
                        .setMessage("") //Prompt message for user
                        .setView(taskEditText) // Allows the view to be edited
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(taskEditText.getText());
                                addList(task, userID, context);
                                loadTaskList();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .create();
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Method that will be used by the delete button
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

    public void innerList(View view) {
        View parent = (View)view.getParent();
        TextView taskTextView = (TextView)parent.findViewById(R.id.list_title);
        Log.e("String", (String) taskTextView.getText());
        String task = String.valueOf(taskTextView.getText());

        Intent i = new Intent(this, EditListView.class);
        i.putExtra("ListName", task);
        i.putExtra("UserID", userID);
        startActivity(i);
    }

    public void storeSelect(View view) {
        View parent = (View)view.getParent();
        TextView taskTextView = (TextView)parent.findViewById(R.id.list_title);
        Log.e("String", (String) taskTextView.getText());
        String task = String.valueOf(taskTextView.getText());

        Intent i = new Intent(this, StoreView.class);
        i.putExtra("ListName", task);
        i.putExtra("UserID", userID);
        startActivity(i);
    }

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

                        if(response.length() >= 1){
                            LID = response;
                            lists.add(key1);
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

                        if(response.length() > 3){
                            lists.remove(Name);
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
                params.put("Users_UserID", Users_UserID);
                params.put("Name", Name);

                return params;
            }
        };


        queue.add(postRequest);
    }

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

        this.setTitle("Your Lists");
        loadTaskList();
    }

}