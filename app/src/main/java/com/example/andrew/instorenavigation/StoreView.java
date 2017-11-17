package com.example.andrew.instorenavigation;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by Matthew Catron on 11/17/2017.
 */

public class StoreView {
    private static ArrayList<Integer> products;
    private static String store;

    StoreView (ArrayList<Integer> products) {
        this.products = products;

    }

    //Method will use a query to select the stores that hold all of the products.
        //Will then change the display to show all the store names with a button to
        //navigate.
    private void getStores() {
        ArrayList<String> stores;
        //TODO Use a php to get store names; store those name in 'stores'

        //TODO Update storeView.xml
    }

    //TODO Create a button that will set the store to pass to Path class, an then redirect to NavigationView
    public void onClick(View v) {
        this.store = "";
    }

    public static String getStore() {
        return store;
    }

    public static ArrayList<Integer> getProducts() {
        return products;
    }
}
