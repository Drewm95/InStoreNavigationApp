package com.example.andrew.instorenavigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**

 */

public class SettingsView extends AppCompatActivity{
    EditText strideLength;
    SeekBar stepSensitivityBar;
    String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent parent = getIntent();

        UserID = parent.getStringExtra("UserID");

        setContentView(R.layout.activity_edit_settings);
        super.setTitle("Settings");

        strideLength = (EditText)findViewById(R.id.editTextStride);

        //get the sensitivity bar
        stepSensitivityBar = (SeekBar)findViewById(R.id.sensitivitySeekBar);

        queryGetWalkData();
    }

    public void queryGetWalkData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String responseValue = null;

        //Query through insertUser.php
        String url = "http://34.238.160.248/getWalkData.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        //If "Good", account created.
                        if (response.equals("bad")) {
                            Context appContext = getApplicationContext();
                            CharSequence text = "An Error Occurred, Please Try Again";

                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();

                            //Eject User to force reload.
                            finish();
                        } else {
                            parseData(response);
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
            //Send input to php.
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Users_UserID", UserID);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void querySetWalkData(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String responseValue = null;

        //Query through insertUser.php
        String url = "http://34.238.160.248/setWalkData.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        //If "Good", account created.
                        if (response.equals("bad")) {
                            Context appContext = getApplicationContext();
                            CharSequence text = "An Error Occurred, Please Try Again";

                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();

                            //Finish the activity, forcing the user back to Login.
                            finish();
                        }else {
                            Context appContext = getApplicationContext();
                            CharSequence text = "Settings Saved";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();

                            finish();
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
            //Send input to php.
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Users_UserID", UserID);
                params.put("Length", strideLength.getText().toString());
                params.put("Sensitivity", Integer.toString(stepSensitivityBar.getProgress()));

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void parseData(String data) {
        int i = 0;
        int commaCount = 0;

        for (int j = 0; j < data.length(); j++) {
            if (data.substring(j,j+1).equals("`")) {
                commaCount++;
                if (commaCount == 1) {
                    String temp = data.substring(i,j);
                    strideLength.setText(temp);
                } else {
                    commaCount=0;
                    String temp = data.substring(i,j);
                    int temp2 = Integer.parseInt(temp);
                    stepSensitivityBar.setProgress(temp2);
                }
                i = j+1;
            }
        }
    }
}
