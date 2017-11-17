package com.example.andrew.instorenavigation;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrew on 11/9/17.
 * The Interactor will update the presenter when called by a view, so that the presenter can
 * receive data from the other classes.
 * Responsible for getting and sending data to the database.
 */

public interface Interactor {
    //Will store a user's itemlist into the database
    public void query(final String key1, final String key2);
}
