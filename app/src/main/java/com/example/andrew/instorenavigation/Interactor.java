package com.example.andrew.instorenavigation;

import java.util.ArrayList;

/**
 * Created by Andrew on 11/9/17.
 * The Interactor will update the presenter when called by a view, so that the presenter can
 * receive data from the other classes.
 * Responsible for getting and sending data to the database.
 */

public class Interactor {

    //Will store a user's itemlist into the database
    public static void sendList() {

    }

    //Edit itemlist in current database
    public static void  updateList(int LID) {

    }

    //Will get a itemlist from the database and return it to the user.
    public String[] getList(int LID) {
        return null;
    }

    //Will get all of the lists to display to the user
    public ArrayList<String[]> getAllLists() {
        return null;
    }
 }
