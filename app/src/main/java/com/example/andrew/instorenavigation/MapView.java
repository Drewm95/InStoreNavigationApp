package com.example.andrew.instorenavigation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/***********************************************************************************************
 THE MAPVIEW IS RESPONSIBLE FOR FETCHING AND PRESENTING THE GRAPHICAL MAP OF THE STORE TO THE
 USER.
 **********************************************************************************************/

public class MapView extends AppCompatActivity {
    String url;
    ImageView map;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        Intent parent = getIntent();
        url = parent.getStringExtra("MapLink");

        map = findViewById(R.id.map);
        new LoadMap(url, map).execute();
//        map.setImageBitmap(getBitmapFromURL(url));
    }

    @Override
    // ---------- Creates the close button  ----------
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map,menu);

        //Change menu icon color
        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);

        return super.onCreateOptionsMenu(menu);
    }

    @Override // handle the user clicking the close button
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
