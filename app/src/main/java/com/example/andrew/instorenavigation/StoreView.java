package com.example.andrew.instorenavigation;

import android.app.Activity;
import android.util.Log;
import android.view.View;

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

public class StoreView extends Activity {
    private String listID;
    private static ArrayList<Integer> products;
    private static String store;
    private int storeID;

    StoreView (ArrayList<Integer> products, String listID) {
        this.products = products;
        this.listID = listID;
    }

    //Method will use a query to select the stores that hold all of the products.
        //Will then change the display to show all the store names with a button to
        //navigate.
    private void getStores() {
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
                            //TODO Append the Store to the current list of stores
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
                params.put("listID", listID);
                params.put("listID", listID);

                return params;
            }
        };
        queue.add(postRequest);
    }

    //TODO Create a button that will set the store to pass to Path class, an then redirect to NavigationView
    public void onClick(View v) {

    }
}
