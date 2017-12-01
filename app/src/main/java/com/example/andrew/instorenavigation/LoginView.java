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
import android.util.Base64;
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

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class LoginView extends Activity implements View.OnClickListener{

    //Declare buttons and textView
    TextView emailView;
    TextView passwordView;
    Button loginButton;
    private String userID;
    private String passHash;

    private static final String ALGORITHM = "AES";
    private static final String KEY = "hu098dAb7hSAk7g3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_view);

        //get the textViews
        emailView = findViewById(R.id.emailView);
        passwordView = findViewById(R.id.passwordView);

        //get the button
        loginButton = findViewById(R.id.loginButton);

        //set the click listener
        loginButton.setOnClickListener(this);
    }


    //Login button calls for authentication
    @Override
    public void onClick(View v) {
        if(v == loginButton)
            try {
                passHash = encrypt(passwordView.getText().toString());
            } catch (Exception e) {

            }
        AuthenticateLogin(v);
    }

    private static String encrypt (String value) throws Exception{
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(LoginView.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
        return encryptedValue64;
    }

    private static Key generateKey() throws Exception{
        Key key = new SecretKeySpec(LoginView.KEY.getBytes(), LoginView.ALGORITHM);
        return key;
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
                params.put("password", passHash);

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
