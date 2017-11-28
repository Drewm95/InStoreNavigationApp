/*************************************************************************************************
 THE CREATE ACCOUNT VIEW IS ONLY ACCESSIBLE THROUGH THE INITIAL LOGIN SCREEN. USER WILL INPUT AN
 EMAIL AND PASSWORD TO CREATE AN ACCOUNT (DUPLICATE ACCOUNTS ARE NOT ALLOWED). THE ACCOUNT WILL BE
 ACCESSIBLE FROM ANY ANDROID DEVICE WITH THE APPLICATION INSTALLED.

 CURRENTLY THE EMAIL HAS NO FORM OF VALIDATION, SO THE USER COULD ENTER ANY STRING IN THE EMAIL
 FIELD AND IT WOULD BE ACCEPTED AS A VALID INPUT
 ************************************************************************************************/
package com.example.andrew.instorenavigation;

import android.content.Context;
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

public class CreateAccountView extends AppCompatActivity {

    //Text views from the create account screen.
    EditText email, password1, password2, strideLength;
    SeekBar stepSensitivityBar;


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

         //get the sensitivity bar
        stepSensitivityBar = (SeekBar)findViewById(R.id.sensitivitySeekBar);

    }

    //Function called when the user clicks the "REGISTER" button.
    public void register (View view){
        //check that the passwords match and that the email is correct
        if(password1.getText().toString().equals(password2.getText().toString())){
            queryAccount();
        } else {
            //Return an error message to inform user that they need to match passwords
            Context appContext = getApplicationContext();
            CharSequence text = "Passwords Must Match";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(appContext, text, duration);
            toast.show();
        }


    }

    public void queryAccount() {
        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(this);
        String responseValue = null;

        //Query through insertUser.php
        String url = "http://34.238.160.248/insertUser.php";
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
                }
        ) {
            //Send input to php.
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email.getText().toString());
                params.put("password", password1.getText().toString());
                params.put("strideLength", strideLength.getText().toString());
                params.put("stepSensitivty", Integer.toString(stepSensitivityBar.getProgress()));

                return params;
            }
        };
        queue.add(postRequest);
    }

}
