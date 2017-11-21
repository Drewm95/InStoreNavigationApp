package com.example.andrew.instorenavigation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
 * Created by Matthew Catron on 11/17/2017.
 */

public class StoreView extends AppCompatActivity {
    private String listID;
    private static String store;
    private String storeID;
    String listName;
    String UID;

    private Context context;
    private String StoreID;
    private ArrayList<String> products;
    private String start;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_view);
        context = this;

        Intent load = getIntent();
        listName = load.getStringExtra("ListName");
        UID = load.getStringExtra("userID");
        super.setTitle("Select Store: " + listName);

        queryStores();
    }

    //Method will use a queryNodes to select the stores that hold all of the products.
        //Will then change the display to show all the store names with a button to
        //navigate.
    public void queryStores() {
        ArrayList<String> stores;
        RequestQueue queue = Volley.newRequestQueue(this);
        String responseValue = null;


        String url = "http://34.238.160.248/getStores.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.length() > 1){
                            parseStores(response);
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
                params.put("listID", listName);
                params.put("User_ID", UID);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void queryStoreID(final String storeName) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String responseValue = null;

        String url = "http://34.238.160.248/getStoreID.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.length() > 1){
                            parseStores(response);
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
                params.put("StoreName", "" + storeName);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void parseStores(String stores) {

    }

    public void path(final View v) {
        AlertDialog dialog = new AlertDialog.Builder(this) //Create prompt to ask if user wants to delete a itemlist
                .setMessage("Do you want to navigate this store?") //Prompt message for user
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        View parent = (View)v.getParent();
                        TextView taskTextView = (TextView)parent.findViewById(R.id.list_title);
                        Log.e("String", (String) taskTextView.getText());
                        String task = String.valueOf(taskTextView.getText());

                        Path path = new Path(storeID, products, start, context);
                    }
                })
                .setNegativeButton("No",null)
                .create();
        dialog.show();
    }

}