package com.example.andrew.instorenavigation;

import android.app.Activity;
import android.content.Intent;
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
 * Created by Andrew on 11/9/17.
 * The Interactor will update the presenter when called by a view, so that the presenter can
 * receive data from the other classes.
 * Responsible for getting and sending data to the database.
 */

public class Interactor extends Activity {

    //Will store a user's itemlist into the database
    public static void sendList() {

    }

    //Edit itemlist in current database
    public static void updateList(int LID) {

    }

    //Will get a itemlist from the database and return it to the user.
    public String[] getList(int LID) {
        return null;
    }

    //Will get all of the lists to display to the user
    public ArrayList<String[]> getAllLists() {
        return null;
    }

    //Authenticate User

    public String authenticateuserLogin(final String email,final String password,final View view) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String responseValue = null;


        String url = "http://34.238.160.248/checkLogin.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response != null){
                            //goToListView(view);
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
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };
        queue.add(postRequest);

        return "Done";
    }

    public void goToListView(View view) {
        //Switch view to the list view
        Intent intent = new Intent(this, ListView.class);

        startActivity(intent);
    }
}
