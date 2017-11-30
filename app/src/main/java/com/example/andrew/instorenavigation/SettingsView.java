package com.example.andrew.instorenavigation;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**

 */

public class SettingsView extends AppCompatActivity{
    EditText strideLength;
    SeekBar stepSensitivityBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_settings);
        super.setTitle("Settings");

        //get the text views
        strideLength = (EditText)findViewById(R.id.editTextStride);

        //get the sensitivity bar
        stepSensitivityBar = (SeekBar)findViewById(R.id.sensitivitySeekBar);

    }

    public void queryWalkData() {
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
                        if (response.equals("Good")) {
                            Context appContext = getApplicationContext();
                            CharSequence text = "Account Created Successfully";

                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();

                            //Finish the activity, forcing the user back to Login.
                            finish();
                            //If "User Already Exists" inform user.
                        } else if (response.equals("User Already Exists")) {
                            Context appContext = getApplicationContext();
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(appContext, response, duration);
                            toast.show();
                            //Any other response is a server side error.
                        }else {
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
                });
    }
}
