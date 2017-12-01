/*******************************************************************************************
 STORE VIEW WILL BE ACCES
 ******************************************************************************************/

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoreView extends AppCompatActivity {
    ArrayAdapter<String> mAdapter;
    ArrayAdapter<String> mAdapter2;
    android.widget.ListView storeNames;

    private boolean startHasProducts;
    private String storeID;
    String listName;
    String UID;

    private String productString;
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
        productString = load.getStringExtra("ProductsString");
        UID = load.getStringExtra("UserID");
        listName = load.getStringExtra("ListName");
        super.setTitle("Select Store: " + listName);


        parseItemNames(productString);
        //queryItems();
    }

    //Generate an item for each store.
    public void loadTaskList() {

        try {
            if (mAdapter == null) {
                mAdapter = new ArrayAdapter<String>(this, R.layout.generate_store_view, R.id.store_title, stores);
                mAdapter2 = new ArrayAdapter<String>(this, R.layout.generate_store_view, R.id.store_ID, storeIDs);
                storeNames.setAdapter(mAdapter);
                //handle click on a specific item
                storeNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {

                        path(pos);
                    }
                });
            } else {
                mAdapter = new ArrayAdapter<String>(this, R.layout.generate_store_view, R.id.store_title, stores);
                mAdapter2 = new ArrayAdapter<String>(this, R.layout.generate_store_view, R.id.store_ID, storeIDs);
                storeNames.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                //handle click on a specific item
                storeNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                        path(pos);
                    }
                });
            }

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

    //Find the start node of the store.
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

                        if(response.equals("bad")){
                            Context appContext = getApplicationContext();
                            CharSequence text = "An Error Occurred, PLease Try Again";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();
                        } else {
                            int i = 0;
                            int commaCount = 0;
                            for (int j = 0; j < response.length(); j++) {
                                String check = response.substring(j,j+1);
                                if (check.equals("`")) {
                                    commaCount++;
                                    if (commaCount == 1) {
                                        storeID = response.substring(i, j);
                                    } else {
                                        String temp = response.substring(i, j);
                                        startHasProducts = temp.equals("0");
                                    }
                                    i = j+1;
                                }
                            }

                            Path path = new Path(storeID, products, storeID, context);
                            Context appContext = getApplicationContext();
                            CharSequence text = "Calculating Path. Please Wait.";
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
                params.put("Store_SID", storeID);

                return params;
            }
        };
        queue.add(postRequest);
    }

    //Parse store names.
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

    //Parse name of the items in the list.
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

    //When a store is selected, a shortest path will be calculated for that store.
    public void path(final int position) {


        AlertDialog dialog = new AlertDialog.Builder(this) //Create prompt to ask if user wants to delete a itemlist
                .setMessage("Do you want to navigate this store?") //Prompt message for user
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
/*
                        View parent = (View)v.getParent();
                        TextView taskTextView = (TextView)parent.findViewById(R.id.store_ID);
                        Log.e("String", (String) taskTextView.getText());
                        String task = String.valueOf(taskTextView.getText());
*/
                        storeID = storeIDs.get(position);

                        queryStart();
                    }
                })
                .setNegativeButton("No",null)
                .create();
        dialog.show();


    }

    //When a path is calculated, the user will be redirected to navigation view.
    public void goToNavView(String path){
        //Switch view to the navigation view
        Intent intent = new Intent(context, NavigationView.class);
        intent.putExtra("Path", path);
        intent.putExtra("ListName", listName);
        intent.putExtra("UserID", UID);
        intent.putExtra("StartHasProducts", startHasProducts);
        intent.putExtra("StoreID", storeID);
        startActivity(intent);
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
                i.putExtra("UserID", UID);
                startActivity(i);
                break;

            default:

        }
        return true;
    }

    //User can click cancel to return to List View
    public void  back(final View view) {
        finish();
    }
}