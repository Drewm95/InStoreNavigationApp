package com.example.andrew.instorenavigation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateAccount extends AppCompatActivity {

    EditText username, password1, password2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //get all of the text views
         username = (EditText)findViewById(R.id.editTextEmail1);
         password1 = (EditText)findViewById(R.id.editTextPassword1);
         password2 = (EditText)findViewById(R.id.editTextPassword2);

        //get button
        Button submitButton = (Button)findViewById(R.id.createAccountButton);

    }

    public void createAccount (View view){

        //check that the passwords match and that the email is correct
        if(password1.getText().toString() == password2.getText().toString()){
            //the passwords match


        }


    }

}
