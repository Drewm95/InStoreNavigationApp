package com.example.andrew.instorenavigation;

/*
 *The LoginView class will provide a login page for the user.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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


    @Override
    public void onClick(View v) {
        if(v == loginButton)
        AuthenticateLogin(v);
    }


    /*
    THIS AUTHENTICATES THE LOGIN WITH THE DATABASE
     */

    public void AuthenticateLogin(final View view) {

        //Get the email and password entered by the user
        final String email = emailView.getText().toString();
        final String password = passwordView.getText().toString();

        //Hash the password

        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(this);
        String responseValue = null;


        String url = "http://34.238.160.248/checkLogin.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.length() > 1){
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

    public void goToListView(View view) {
        //Switch view to the list view
        Intent intent = new Intent(this, ListView.class);

        startActivity(intent);
    }
}
