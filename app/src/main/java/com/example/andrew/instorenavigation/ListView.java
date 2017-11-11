package com.example.andrew.instorenavigation;

/**
 * Created by Andrew on 11/9/17.
 * The ListView Class will allow the user to create a new list, see all of their lists, and delete
 * lists. It will also allow them to select a list to begin navigation.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ListView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
    }
}
