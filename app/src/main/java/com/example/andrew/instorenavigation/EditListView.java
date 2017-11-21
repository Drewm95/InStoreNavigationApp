//***********************************************
package com.example.andrew.instorenavigation;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * This code was created with the help of
 * https://www.youtube.com/watch?v=RXtj4TxMmW0
 */

public class EditListView extends AppCompatActivity {

    ItemDbHelper itemDbHelper;
    ArrayAdapter<String> mAdapter;
    android.widget.ListView lstTask;
    //private int userID;
    private String userID, listName;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list_view);
        context = this;
        itemDbHelper = new ItemDbHelper(this);

        lstTask = findViewById(R.id.edit_list);
        Intent loginID = getIntent();
        if(loginID.hasExtra("UserID")){
            userID = loginID.getStringExtra("userId");
        }
        if(loginID.hasExtra("ListName")){
            listName = loginID.getStringExtra("ListName");
            super.setTitle(listName);
        }
        //TODO Add an error message if the extras are not present and kick user back to list view
        //userID = Integer.parseInt(loginID.getStringExtra("userID"));
       // userID = loginID.getStringExtra("userID");
        //Bundle extras = loginID.getExtras();
        // userID = extras.getString("userID");
        loadTaskList();

    }

    private void loadTaskList() {
        ArrayList<String> taskList = itemDbHelper.getTaskList();
        if(mAdapter==null){
            mAdapter = new ArrayAdapter<String>(this,R.layout.generate_edit_list_view,R.id.item_title,taskList);
            lstTask.setAdapter(mAdapter);
        }
        else{
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
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
                        .setTitle("Add New Item?")
                        .setMessage("") //Prompt message for user
                        .setView(taskEditText) // Allows the view to be edited
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(taskEditText.getText());
                                query(listName, userID, context);

                                itemDbHelper.insertNewTask(task);
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
                        TextView taskTextView = (TextView)parent.findViewById(R.id.item_title);
                        Log.e("String", (String) taskTextView.getText());
                        String task = String.valueOf(taskTextView.getText());
                        itemDbHelper.deleteTask(task);

                        loadTaskList();
                    }
                })
                .setNegativeButton("Cancel",null)
                .create();
        dialog.show();


    }




    public void query(final String key1, final String key2, Context context) {
        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(context);
        String responseValue = null;


        String url = "http://34.238.160.248/GetListContents.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.length() > 1){



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

                return params;
            }
        };
        queue.add(postRequest);
    }


}

