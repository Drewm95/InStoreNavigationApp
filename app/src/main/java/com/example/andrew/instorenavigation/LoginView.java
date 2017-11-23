package com.example.andrew.instorenavigation;

/***********************************************************************************************
 THE LOGINVIEW CLASS WILL PROVIDE A LOGIN PAGE FOR THE USER. USER WILL BE ABLE TO ACCESS
 CREATEACCOUNTVIEW THOROUGH THE REGISTER BUTTON. GIVEN VALID INPUTS FROM THE USER, THEY WILL
 BE REDIRECTED TO THE LISTVIEW ACTIVITY. THE USERID WILL BE PASSED FORWARD FROM LOGIN
 AUTHENTICATION.
 **********************************************************************************************/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginView extends Activity implements View.OnClickListener{

    //Declare buttons and textView
    TextView emailView;
    TextView passwordView;
    Button loginButton;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_view);

        //get the textViews
        emailView = (TextView) findViewById(R.id.emailView);
        passwordView = (TextView) findViewById(R.id.passwordView);


        //get the button
        loginButton = (Button) findViewById(R.id.loginButton);

        //set the click listener
        loginButton.setOnClickListener(this);
    }


    //Login button calls for authentication
    @Override
    public void onClick(View v) {
        if(v == loginButton)
            AuthenticateLogin(v);
    }

    //Authentica login with information in the database.
    public void AuthenticateLogin(final View view) {

        //Get the email and password entered by the user
        final String email = emailView.getText().toString();
        final String password = passwordView.getText().toString();

        //Hash the password

        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(this);
        String responseValue = null;

        //Query through checkLogin.php
        String url = "http://34.238.160.248/checkLogin.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        //Unexpected response. Error on server side.
                        if(response.equals("Bad")){
                            Context appContext = getApplicationContext();
                            CharSequence text = "An Unexpected Error Occurred";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();
                        //Response returns empty string meaning that the user has entered
                            //inlaid credentials/
                        } else if (response.equals("")){
                            Context appContext = getApplicationContext();
                            CharSequence text = "Email or Password is Incorrect";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(appContext, text, duration);
                            toast.show();
                        //Response returns UserID meaning that the information is valid.
                        } else {
                            //Login is valid so the ListView Activity is started.
                            userID = (response);
                            goToListView(view);
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
            //Give inputs to php
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };
        queue.add(postRequest);

    }

    //Start List View Activity.
    public void goToListView(View view) {
        //Switch view to the list view
        Intent intent = new Intent(this, ListView.class);
        //Pass forward UserID to serve user specific information.
        intent.putExtra("userID", userID );
        startActivity(intent);
    }

    //If "REGISTER" is clicked, start CreateAccount Activity
    public void register(View v) {
        Intent intent = new Intent(this, CreateAccountView.class);
        startActivity(intent);
    }
}
