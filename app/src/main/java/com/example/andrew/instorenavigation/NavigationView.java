package com.example.andrew.instorenavigation;

/*
 * NavigationView displays everything the user needs to know to navigate along their shortest path.
 * It will display the item the user is currently navigating to in text and an arrow pointing in
 * the direction needed to travel to get to the isle the item is on. The user will also be able to
 * swipe the item’s text left or right to say if the item was picked up or not. Finally, the list
 * will be displayed at the bottom of the GUI which will allow the user to scroll through all of
 * their items.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NavigationView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_view);
    }
}
