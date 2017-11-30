/*
 * NavigationView displays everything the user needs to know to navigate along their shortest path.
 * It will display the item the user is currently navigating to in text and an arrow pointing in
 * the direction needed to travel to get to the isle the item is on. The user will also be able to
 * swipe the item’s text left or right to say if the item was picked up or not. Finally, the itemlist
 * will be displayed at the bottom of the GUI which will allow the user to scroll through all of
 * their items.
 */
package com.example.andrew.instorenavigation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;

public class NavigationView extends AppCompatActivity implements SensorEventListener {

    //declare the sensor manager
    private SensorManager sensorManager;
    private Sensor mAccel,mMag, mla;
    private int stepCount, targetDistance, turnCode, navStep, azimuthCheck;
    private Date lastUpdate;
    private float lastZ, newZ,newX, newY, lastx, lasty,stepDistRatio, stepSense;
    private float lastAzimuth, rotation;
    private double azumuthStart, azimuth, degreesOver360Low, degreesOver360High;
    private TextView  instructionView, currentItemView;
    public static float[] mAccelerometer = null;
    public static float[] mGeomagnetic = null;
    private ImageView arrow;
    private Integer instructionString;
    private ArrayList<int[]> directionList = new ArrayList<int[]>();
    private boolean turnComplete;
    private  AlertDialog lowAccuracyAlert;
    private String userID, listName, storeID;
    private int nextNodeID;
    private boolean waitForConfirmation;
    private String[] productsAtNode;

    //Arraylist to hold all items inside of the list.
    private ArrayList<String> items;

    //Adapter is used to display every item contained within a list.
    ArrayAdapter<String> mAdapter;
    android.widget.ListView lstTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the extra
        Intent parentIntent = getIntent();

        //the path string
        String pathString = "";
        if(parentIntent.hasExtra("Path")){
            pathString = parentIntent.getStringExtra("Path");
        }
        if(parentIntent.hasExtra("UserID")){
            userID = parentIntent.getStringExtra("UserID");
        }
        if(parentIntent.hasExtra("ListName")){
            listName = parentIntent.getStringExtra("ListName");
        }
        if(parentIntent.hasExtra("StoreID")){
            storeID = parentIntent.getStringExtra("StoreID");
        }

        azimuth = 0;
        turnComplete = false;

        //parse the path
        parseDetailedPath(pathString);

        //set strideLength
        stepDistRatio = 24; //Stride length of 2 feet

        setContentView(R.layout.activity_navigation_view);

        //instatiate the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Get the textViews
        //lowAccuracyWarning = (TextView)findViewById(R.id.lowAccuracyWarning);
        //stepCountView = (TextView)findViewById(R.id.NavStepCount);
        arrow = (ImageView)findViewById(R.id.arrowView);
        instructionView = (TextView)findViewById(R.id.InstructionView);
        currentItemView = (TextView)findViewById(R.id.currentProductView);


        //get the default accelerometer from the sm
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            //there is a accelerameter
            mAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        } else {

        }

        //get the default magnetometer from the sm
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            //there is a magnetometer
            mMag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            //DEBUG TEXT
            //mag1.setText("SENSOR FOUND");
        } else {
            // mag1.setText("SENSOR NOT PRESENT");
        }


        //get the default step counter from the sm
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            //there is a step counter

            mla = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        } else {

        }

        //set all of the required initial last acceleration values
        lastZ = 0; //TODO do this better
        lastx= 0;
        lasty = 0;
        stepSense = 1;
        //get the time, this is used to prevent counting a single step multiple times
        lastUpdate = Calendar.getInstance().getTime();

        //get the low accuracy message ready in case it is needed
        AlertDialog.Builder builder = new AlertDialog.Builder(NavigationView.this);
        builder.setTitle("Sensor Calibration Required");
        builder.setMessage("Please move your phone around in an infinity pattern for 10 seconds.");

        lowAccuracyAlert = builder.create();

        //Do not move this, the view has to have been made first before it can be referenced.
        //get the reference to the navigation screen's list view
        lstTask = findViewById(R.id.listsNav);

        //Instantiate items array.
        items = new ArrayList<>();

        //query for the list items and populate list view
        queryItems();

        //Get the user's details
        getUserDetails(userID);

        //hide the current item view
        //currentItemView.setVisibility(View.INVISIBLE);

        //by default set wait to false
        waitForConfirmation = false;

        demoSetup();

    }

    @Override
    protected void onResume(){
        super.onResume();
        getUserDetails(userID);// in case they go to settings and change their sensitivity
        sensorManager.registerListener(this, mAccel , SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mMag, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mla, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor == mMag) {

            //assign the new event values to the magnetometer array for use with finding direction
            mGeomagnetic = event.values;

        }

        if (event.sensor == mAccel) {

            //assign the new event values to the magnetometer array for use with finding direction
            mAccelerometer = event.values;

        }
/*
        if (event.sensor == mstep) {

            stepCountReal++;
           countChecker.setText(Float.toString(stepCountReal));
        }
        */

        if(event.sensor == mla){

            newX = event.values[0];
            newY = event.values[1];
            newZ = event.values[2];
            //check if the new value is much different than the last one
            if (newZ   > lastZ  + stepSense) {

                //make sure it has been at least 10 milliseconds since the last step count
                //if not don't count it, people don't walk that fast
                if (Calendar.getInstance().getTimeInMillis() > lastUpdate.getTime() + 500 && turnComplete) {
                    //A step was detected
                    stepCount++;
                    lastUpdate = Calendar.getInstance().getTime();

                    //This is to prevent the azimuth start from hanging at 0 upon stating navigation
                    if(azumuthStart == 0.0){
                        azumuthStart = azimuth;

                        if(azumuthStart + 75 >= 360){
                            double degreesUnder360 =  360 - azumuthStart;
                            degreesOver360Low = 75 - degreesUnder360;
                            degreesOver360High = 105 - degreesUnder360;
                        }

                    }

                    updateArrow();
                }
            } else {

            }


            //assign value to lastxyz before moving on
            lastZ = newZ;
            lastx = newX;
            lasty = newY;
        }

        /*

        TEST CODE FOR GETTING DIRECTIONS USING THE MAGNETIC FIELD SENSOR

         */

        if (mAccelerometer != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mAccelerometer, mGeomagnetic);

            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // at this point, orientation contains the azimuth(direction), pitch and roll values.
                azimuth = (180 * orientation[0] / Math.PI) + 180;

            }

            if (Calendar.getInstance().getTimeInMillis() > lastUpdate.getTime() + 500 && !turnComplete) {
                //A step was detected
                //lastUpdate = Calendar.getInstance().getTime();

                if(azumuthStart == 0.0){
                    azumuthStart = azimuth;

                    if(azumuthStart + 75 >= 360){
                        double degreesUnder360 =  360 - azumuthStart;
                        degreesOver360Low = 75 - degreesUnder360;
                        degreesOver360High = 105 - degreesUnder360;
                    }

                }

                updateArrow();
            }


        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
            if(sensor == mMag){
                if(accuracy < 3) {
                        lowAccuracyAlert.show();
                }
                if(accuracy > 2 && lowAccuracyAlert.isShowing()){
                    try {
                        lowAccuracyAlert.hide();
                    }
                    catch (Exception e){
                        System.out.println("Failed to hide dialog");
                    }
                }
            }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void updateArrow(){


        int turnAngle = 75;
        System.out.println("Steps: " + stepCount + "      azimuth: "  + azimuth + "          azimuthStart: " + azumuthStart + "      Z Acceleration: " + newZ + "Azimuth Check: " + azimuthCheck);

        //Forward
        if(turnCode == 0) {
            arrow.setImageDrawable(getDrawable(R.drawable.arrowforward));
            arrow.setRotation(0);
            turnComplete = true;

            if (stepCount >= (float) (targetDistance / stepDistRatio)) {

                if(items != null) {
                    if (items.size() > 1) {
                        //check if the now current item on the list is at the current node, if so the wait for confirmation will return to true
                        queryProductAtNode(storeID, Integer.toString(nextNodeID));
                        //check the current product against the new list of products
                        if(productsAtNode != null) {
                            for (String product : productsAtNode) {
                                if (!items.contains(product)) {
                                    //Update instructions
                                    navStep++;
                                    stepCount = 0;
                                    turnComplete = false;
                                    CheckNavigation();
                                }
                                else {
                                    arrow.setImageDrawable(getDrawable(R.drawable.acceptarrow));
                                    arrow.setRotation(0);
                                }
                            }
                        }
                        else{
                            //Update instructions
                            navStep++;
                            stepCount = 0;
                            turnComplete = false;
                            CheckNavigation();
                        }
                    }

                }
                else{
                    //Update instructions
                    navStep++;
                    stepCount = 0;
                    turnComplete = false;
                    CheckNavigation();
                }

            }
        }

        //Right

        if(turnCode == 1) {

            //check if they completed the turn
            if(!turnComplete) {
                arrow.setImageDrawable(getDrawable(R.drawable.arrowforwardfill));
                arrow.setRotation(90);
                instructionView.setText("Turn Right Now");
                //if(((azimuth > azumuthStart + turnAngle && azumuthStart < turnAngle) || (azimuth > azumuthStart - 285 && azimuth < 0 && azumuthStart > turnAngle)) && (newZ < 0.8 && newZ > -0.8 && abs(newZ - lastZ) < 1)){


                if(((azimuth - azumuthStart >= 75 && azimuth - azumuthStart <= 105) || (azumuthStart + 75 >= 360 && azimuth > degreesOver360Low && azimuth < degreesOver360High)) && (newZ < 0.8 && newZ > -0.8 && abs(newZ - lastZ) < 1)){


                    if(azimuthCheck > 4) {
                        arrow.setImageDrawable(getDrawable(R.drawable.arrowforward));
                        arrow.setRotation(0);
                        instructionView.setText("Walk Forward " + targetDistance / 12 + " feet");
                        turnComplete = true;
                        azimuthCheck = 0;
                    }
                    else{
                        azimuthCheck++;
                    }
                }
                else{
                    //whoops, the conditions for a turn were not met five times in a row, set the azimuth check to 0 and try again. The azimuth must have teh 90 degree difference sustained for at least 5 cycles for the reported reading to be considered correct
                    azimuthCheck = 0;
                }
            }
            //now check if they completed the distance portion
            else if (stepCount >= (float) (targetDistance / stepDistRatio)) {

                if(!waitForConfirmation) {
                    //Update instructions
                    navStep++;
                    stepCount = 0;
                    turnComplete = false;
                    CheckNavigation();
                }
                else{
                    arrow.setImageDrawable(getDrawable(R.drawable.acceptarrow));
                    arrow.setRotation(0);
                }

            }
        }

        //Right

        if(turnCode == 3) {

            //check if they completed the turn
            if(!turnComplete) {
                arrow.setImageDrawable(getDrawable(R.drawable.arrowforwardfill));
                arrow.setRotation(270);
                instructionView.setText("Turn Left Now");

                if(((azimuth - azumuthStart <= -75 && azimuth - azumuthStart >= -105) || (azumuthStart + 75 >= 360 && azimuth > degreesOver360Low && azimuth < degreesOver360High)) && (newZ < 0.8 && newZ > -0.8 && abs(newZ - lastZ) < 1)){
                    if(azimuthCheck > 8) {
                        arrow.setImageDrawable(getDrawable(R.drawable.arrowforward));
                        arrow.setRotation(0);
                        instructionView.setText("Walk Forward " + targetDistance / 12 + " feet");
                        turnComplete = true;
                        azimuthCheck = 0;
                    }
                    else{
                        azimuthCheck++;
                    }
                }
                else{
                    //whoops, the conditions for a turn were not met five times in a row, set the azimuth check to 0 and try again. The azimuth must have teh 90 degree difference sustained for at least 5 cycles for the reported reading to be considered correct
                    azimuthCheck = 0;
                }
            }
            //now check if they completed the distance portion
            else if (stepCount >= (float) (targetDistance / stepDistRatio)) {

                if(!waitForConfirmation) {
                    //Update instructions
                    navStep++;
                    stepCount = 0;
                    turnComplete = false;
                    CheckNavigation();
                }
                else{
                    arrow.setImageDrawable(getDrawable(R.drawable.acceptarrow));
                    arrow.setRotation(0);
                }

            }
        }

    }

    private void demoSetup(){

        navStep = 0;
        CheckNavigation();
        updateArrow();
    }

    protected void CheckNavigation(){
        int directionArray[] = new int[2];
        if(navStep < directionList.size()) {
            directionArray = directionList.get(navStep);
            updateNav(directionArray[0], directionArray[1], directionArray[2]);

        }
        else{

            if(items.size() < 1) {
                //Make and show a complete message
                arrow.setVisibility(View.INVISIBLE);
                instructionView.setVisibility(View.INVISIBLE);
                toastMessage("Navigation Complete");
                finish();
            }
            else{
                arrow.setVisibility(View.INVISIBLE);
                instructionView.setText("Please check off remaining items");
            }
        }

    }

    protected void updateNav(int distance, int tcode, int nodeID){
        //Update the global variables and the navarrow indicator
        targetDistance = distance;
        turnCode = tcode;
        //we need to know what angle the phone was at the start so we know when they complete the turn
        azumuthStart = azimuth;

        if(azumuthStart + 75 >= 360){
            double degreesUnder360 =  360 - azumuthStart;
            degreesOver360Low = 75 - degreesUnder360;
            degreesOver360High = 105 - degreesUnder360;
        }

        //create the instruction string
        if(turnCode == 0){
            instructionView.setText("Walk Forward " + distance / 12 + " feet");
        }
        else if( turnCode == 3){
            //instructionView.setText("Turn Left in "+ distance / 12 + " feet");

        }
        else if( turnCode == 1){
            //instructionView.setText("Turn Right in "+ distance / 12 + " feet");

        }
        //get the node ID
        nextNodeID = nodeID;

        queryProductAtNode(storeID, Integer.toString(nextNodeID));


    }

    private void parseDetailedPath (String path) {
        int i = 0;
        int commaCount = 0;
        int tempDirection = 0, tempDistance = 0, tempStopNode = 0;
        String temp = path.replace("[", ""); temp = temp.replace("]", "");

        for (int j = 0; j < temp.length(); j++) {
            if (temp.substring(j,j+1).equals(",")) {
                commaCount++;
                if (commaCount == 1) {
                    tempDirection = Integer.parseInt(temp.substring(i,j));
                } else if (commaCount == 2){
                    commaCount++;
                    tempDistance = Integer.parseInt(temp.substring(i,j));
                } else {
                    commaCount=0;
                    tempStopNode = Integer.parseInt(temp.substring(i,j));
                    int tempArray[] = new int[]{tempDistance, tempDirection, tempStopNode};
                    directionList.add(tempArray);
                }
                i = j+1;
            }
        }


    }


    public void queryMap(View view){
        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(this);
        final String responseValue = null;


        String url = "http://34.238.160.248/getMapLink.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.equals("bad")){
                            toastMessage("No Map For This Store");
                        } else {
                            showMap(response);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Store_SID", storeID);

                return params;
            }
        };
        queue.add(postRequest);
    }

    private void showMap(String url) {
        Intent intent = new Intent(this, MapView.class);
        intent.putExtra("MapLink", url);
        startActivity(intent);
    }

// ---------- Query Items ----------
    private void queryItems() {
        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(this);
        final String responseValue = null;


        String url = "http://34.238.160.248/GetListContent.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.length() >= 1){
                            parseItemNames(response);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("List_Name", listName);
                params.put("Users_UserID", userID);

                return params;
            }
        };
        queue.add(postRequest);
    }

    // ---------- Parse Item Names ----------
    private void parseItemNames(String items) {
        int i = 0;

        for (int j = 0; j < items.length(); j++) {
            String check = items.substring(j,j+1);
            if (check.equals("`")) {
                String temp = items.substring(i,j);
                this.items.add(temp);
                i = j+1;
            }
        }

        loadTaskList();
    }

    // ---------- Load Task List ----------
    private void loadTaskList() {
        if(mAdapter==null){
            mAdapter = new ArrayAdapter<String>(this,R.layout.generate_edit_list_view_nav,R.id.item_title,items);
            lstTask.setAdapter(mAdapter);//Populates the contents of the EditListView

            //Add first item to bottom text
            currentItemView.setText(lstTask.getItemAtPosition(0).toString());
            items.remove(0);
            lstTask.invalidateViews();


        }
        else{
            mAdapter = new ArrayAdapter<String>(this,R.layout.generate_edit_list_view_nav,R.id.item_title,items);
            lstTask.setAdapter(mAdapter);//Populates the contents of the EditListView
            mAdapter.notifyDataSetChanged();
        }
    }

    //--------- Get the Stride Length and Step Sensitivity -----------------
    private void getUserDetails(final String UserID){

        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(this);
        final String responseValue = null;


        String url = "http://34.238.160.248/getWalkData.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.length() >= 1){
                            String[] details=response.split("`");
                            stepDistRatio = Float.parseFloat(details[0]); //store the user's stride length
                            stepSense = Float.parseFloat(details[1]); //store the user's step Sensitivity

                            toastMessage("Step Sensitivity: " + stepSense);
                            System.out.println(stepSense + "     " + stepDistRatio);

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Users_UserID", UserID);

                return params;
            }
        };
        queue.add(postRequest);
    }

    //------ Toast Message --------
    private void toastMessage(String message){
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    @Override
    // ---------- Creates the add button  ----------
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        //Change menu icon color
        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);

        return super.onCreateOptionsMenu(menu);
    }

    @Override // handle the user clicking the settings button
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId()) //get the id which is an int
        {
            case R.id.settings_action:
                Intent i = new Intent(this, SettingsView.class);
                i.putExtra("UserID", userID);
                startActivity(i);
                break;

            default:

        }
        return true;
    }

    //Get the products at a specific node
    private void queryProductAtNode(final String storeId, final String nodeID) {

        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(this);
        String responseValue = null;


        String url = "http://34.238.160.248/getProductsAtNode.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.equalsIgnoreCase("bad")){
                            waitForConfirmation = false;
                            //there are no items here
                        }
                        else if(response.length() >= 1){
                            //store all of the products at this node
                            productsAtNode = response.split("`");
                        }
                        else{
                            waitForConfirmation = false;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<String, String>();
                params.put("Node_NID", nodeID);
                params.put("Store_SID", storeId);
                System.out.println(nodeID +"  " + storeId);
                return params;
            }
        };
        queue.add(postRequest);

    }

    //handle the user checking off an item
    public void CheckOffItem(View view){
        if(items != null) {
            if (items.size() > 1) {
                //check if the now current item on the list is at the current node, if so the wait for confirmation will return to true
                queryProductAtNode(storeID, Integer.toString(nextNodeID));
                //check the current product against the new list of products
                if(productsAtNode != null) {
                    for (String product : productsAtNode) {
                        if (product.equalsIgnoreCase(currentItemView.getText().toString())) {
                            //move the top item in the list view up to the current item text view
                            items.remove(0);
                            lstTask.invalidateViews();
                            currentItemView.setText(lstTask.getItemAtPosition(0).toString());
                            //set the wait for confirmation to zero
                            waitForConfirmation = false;
                            break;
                        }
                    }
                }
            }
        }
        else{
            //they have completed the list, return the user to the list screen
            toastMessage("List Complete");
            finish();
        }
    }

}

