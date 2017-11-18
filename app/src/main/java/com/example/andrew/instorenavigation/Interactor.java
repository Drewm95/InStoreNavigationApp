package com.example.andrew.instorenavigation;

import android.content.Context;

/**
 * Created by Andrew on 11/9/17.
 * The Interactor will update the presenter when called by a view, so that the presenter can
 * receive data from the other classes.
 * Responsible for getting and sending data to the database.
 */

public interface Interactor{
    //Will store a user's itemlist into the database
    public void query(final String key1, final String keys2, final Context context);
}
