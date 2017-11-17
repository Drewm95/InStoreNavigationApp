//***********************************************
package com.example.andrew.instorenavigation;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * This code was created with the help of
 * https://www.youtube.com/watch?v=RXtj4TxMmW0
 */

public class ListView extends AppCompatActivity {

    DbHelper dbHelper;
    ArrayAdapter<String> mAdapter;
    android.widget.ListView lstTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itemlist);

        dbHelper = new DbHelper(this);

        lstTask = (android.widget.ListView)findViewById(R.id.lstTask);

        loadTaskList();
    }

    private void loadTaskList() {
        ArrayList<String> taskList = dbHelper.getTaskList();
        if(mAdapter==null){
            mAdapter = new ArrayAdapter<String>(this,R.layout.itemrow,R.id.task_title,taskList);
            lstTask.setAdapter(mAdapter);
        }
        else{
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        //Change menu icon color
        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_task:
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this) //Create prompt to ask if user wants to create a new itemlist
                        .setTitle("Create New List")
                        .setMessage("Create this itemlist?") //Prompt message for user
                        .setView(taskEditText) // Allows the view to be edited
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(taskEditText.getText());
                                dbHelper.insertNewTask(task);
                                loadTaskList();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .create();
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Method that will be used by the delete button
    public void deleteTask(final View view){ //Method used to delete the itemrow selected
        AlertDialog dialog = new AlertDialog.Builder(this) //Create prompt to ask if user wants to delete a itemlist
                .setMessage("Do you want to delete this itemlist?") //Prompt message for user
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        View parent = (View)view.getParent();
                        TextView taskTextView = (TextView)parent.findViewById(R.id.task_title);
                        Log.e("String", (String) taskTextView.getText());
                        String task = String.valueOf(taskTextView.getText());
                        dbHelper.deleteTask(task);

                        loadTaskList();
                    }
                })
                .setNegativeButton("Cancel",null)
                .create();
        dialog.show();


    }

    public void innerList(View view) {

        Intent i = new Intent(this, EditListView.class);
        startActivity(i);


    }
}

