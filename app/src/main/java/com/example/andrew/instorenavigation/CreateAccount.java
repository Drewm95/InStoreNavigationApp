package com.example.andrew.instorenavigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity {

    EditText email, password1, password2, strideLength;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        super.setTitle("Create Account");

        //get all of the text views
         email = (EditText)findViewById(R.id.editTextEmail);
         password1 = (EditText)findViewById(R.id.editTextPassword1);
         password2 = (EditText)findViewById(R.id.editTextPassword2);
         strideLength = (EditText)findViewById(R.id.editTextStride);

        //get button
        Button submitButton = (Button)findViewById(R.id.registerButton);
    }

    public void register (View view){

        //check that the passwords match and that the email is correct
        if(password1.getText().toString().equals(password2.getText().toString())){
            //the passwords match
            queryAccount();
        } else {
            //TODO Toast some error
        }


    }

    public void queryAccount() {
        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(this);
        String responseValue = null;


        String url = "http://34.238.160.248/insertUser.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if (response.equals("Good")) {
                            Context appContext = getApplicationContext();
                            CharSequence text = "Account Created Successfully";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();
                            finish();
                        } else if (response.equals("User Already Exists")) {
                            Context appContext = getApplicationContext();
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(appContext, response, duration);
                            toast.show();
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
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email.getText().toString());
                params.put("password", password1.getText().toString());
                params.put("strideLength", strideLength.getText().toString());

                return params;
            }
        };
        queue.add(postRequest);
    }

}
