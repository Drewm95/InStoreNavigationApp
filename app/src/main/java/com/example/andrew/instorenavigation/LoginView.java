package com.example.andrew.instorenavigation;

/*
 *The LoginView class will provide a login page for the user.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LoginView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_view);
    }

    public void AuthenticateUserDetails(View view){

        Interactor interactor = new Interactor();
        interactor.authenticateuserLogin("bob@smith.com", "password", view);

    }

    public void goToListView(View view) {


        //Switch view to the list view
        Intent intent = new Intent(this, ListView.class);

        startActivity(intent);
    }

}
