package com.example.andrew.instorenavigation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class StoreView extends AppCompatActivity implements View.OnClickListener {
    ArrayAdapter<String> mAdapter;
    ArrayAdapter<String> mAdapter2;
    android.widget.ListView storeNames;

    private String store;
    private String listID;
    private String storeID;
    String listName;
    String UID;

    private ArrayList<String> stores;
    private ArrayList<String> storeIDs;
    private Context context;
    private ArrayList<String> products;
    private String start;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_view);
        context = this;
        products = new ArrayList<>();
        stores = new ArrayList<>();
        storeIDs = new ArrayList<>();
        storeNames = findViewById(R.id.storeList);

        Intent load = getIntent();
        listName = load.getStringExtra("ListName");
        UID = load.getStringExtra("UserID");
        super.setTitle("Select Store: " + listName);

        //handle click on a specific item
        storeNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                path(position);
            }
        });

        queryItems();
    }

    private void loadTaskList() {

        try {
            mAdapter = new ArrayAdapter<String>(this, R.layout.generate_store_view, R.id.store_title, stores);
            //handle click on a specific item
            storeNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> mAdapter, View view, int position, long id) {
                    path(position);
                }
            });
            storeNames.setAdapter(mAdapter);
        }
        catch (Exception e){
            e.printStackTrace();
        }

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
                params.put("List_Name", listName);
                params.put("Users_UserID", UID);

                return params;
            }
        };
        queue.add(postRequest);
    }

    private void queryStart() {


        RequestQueue queue = Volley.newRequestQueue(context);
        String responseValue = null;


        String url = "http://34.238.160.248/getStoreStart.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.length() >= 1){
                            Path path = new Path(store, products, response, context);
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
                params.put("Store_ID", storeID);

                return params;
            }
        };
        queue.add(postRequest);
    }

    private void queryItems() {
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

                        if(response.length() >= 1){
                            parseItemNames(response);
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
                params.put("Users_UserID", UID);
                //  params.put("Product_Name", items.);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void parseStores(String stores) {
            int i = 0;
            int commacount = 0;
            for (int j = 0; j < stores.length(); j++) {
                String check = stores.substring(j,j+1);
                 if (check.equals("`")) {
                    commacount++;
                    String temp = stores.substring(i,j);
                    if (commacount == 1) {
                        this.stores.add(temp);
                    }else {
                        commacount = 0;
                        this.storeIDs.add(temp);
                    }
                    i = j+1;
                }
            }

            loadTaskList();
    }

    private void parseItemNames(String items) {
        int i = 0;

        for (int j = 0; j < items.length(); j++) {
            String check = items.substring(j,j+1);
            if (check.equals("`")) {
                String temp = items.substring(i,j);
                this.products.add(temp);
                i = j+1;
            }
        }

        queryStores();
    }


    public void path(final int position) {

        AlertDialog dialog = new AlertDialog.Builder(this) //Create prompt to ask if user wants to delete a itemlist
                .setMessage("Do you want to navigate this store?") //Prompt message for user
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        storeID = storeIDs.get(position);
                        queryStart();
                    }
                })
                .setNegativeButton("No",null)
                .create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {

    }
}