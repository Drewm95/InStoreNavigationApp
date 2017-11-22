/*
 * NavigationView displays everything the user needs to know to navigate along their shortest path.
 * It will display the item the user is currently navigating to in text and an arrow pointing in
 * the direction needed to travel to get to the isle the item is on. The user will also be able to
 * swipe the item’s text left or right to say if the item was picked up or not. Finally, the itemlist
 * will be displayed at the bottom of the GUI which will allow the user to scroll through all of
 * their items.
 */
package com.example.andrew.instorenavigation;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NavigationView extends AppCompatActivity implements SensorEventListener {

    //declare the sensor manager
    private SensorManager sensorManager;
    private Sensor mAccel,mMag, mla;
    private int stepCount, stepSense, targetDistance, turnCode, navStep;
    private Date lastUpdate;
    private float lastZ, newZ,newX, newY, lastx, lasty,stepDistRatio;
    private double azumuthStart, azimuth;
    private TextView  instructionView;
    public static float[] mAccelerometer = null;
    public static float[] mGeomagnetic = null;
    private ImageView arrow;
    private Integer instructionString;
    private ArrayList<int[]> directionList = new ArrayList<int[]>();
    private boolean turnComplete;

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
        stepSense = 2;
        //get the time, this is used to prevent counting a single step multiple times
        lastUpdate = Calendar.getInstance().getTime();

        demoSetup();

    }

    @Override
    protected void onResume(){
        super.onResume();
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
        /*
        if(event.sensor == mAccel) {

            //assign the new event values to the accelerometer array for use with finding direction
            mAccelerometer = event.values;

            //get the latest value from the sensor

            newX = event.values[0];
            newY = event.values[1];
            newZ = event.values[2];
            //check if the new value is much different than the last one
            if (newZ + newX + newY > lastZ + lastx + lasty + stepSense) {

                //make sure it has been at least 10 milliseconds since the last step count
                //if not don't count it, people don't walk that fast
                if (Calendar.getInstance().getTimeInMillis() > lastUpdate.getTime() + 500) {
                    //A step was detected
                    stepDetected.setVisibility(View.VISIBLE);
                    stepCount++;
                    stepCountView.setText(Float.toString(stepCount));
                    lastUpdate = Calendar.getInstance().getTime();
                }
            } else {
                stepDetected.setVisibility(View.INVISIBLE);
            }

            //report other readings
            if (newX > lastx + 0.1 || newX < lastx - 0.1)
                x.setText(Float.toString(event.values[0]));
            if (newY > lasty + 0.1 || newY < lasty - 0.1)
                y.setText(Float.toString(event.values[1]));
            if (newZ > lastZ + 0.1 || newZ < lastZ - 0.1)
                z.setText(Float.toString(event.values[2]));

            //get the average z
            count++;
            zSum += newZ;
            avgZ = zSum / count;
            avgZText.setText(Float.toString(avgZ));

            //assign value to lastxyz before moving on
            lastZ = newZ;
            lastx = newX;
            lasty = newY;
        }

        */

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
                if (Calendar.getInstance().getTimeInMillis() > lastUpdate.getTime() + 500) {
                    //A step was detected
                    stepCount++;
                    lastUpdate = Calendar.getInstance().getTime();
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
                azimuth = (double)(180 * orientation[0] / Math.PI);

            }

            if (Calendar.getInstance().getTimeInMillis() > lastUpdate.getTime() + 500) {
                //A step was detected
                //lastUpdate = Calendar.getInstance().getTime();
                updateArrow();
            }


        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    private void updateArrow(){

        //Forward
        if(turnCode == 0) {
            arrow.setImageDrawable(getDrawable(R.drawable.arrowforward));
            arrow.setRotation(0);

            if (stepCount >= (float) (targetDistance / stepDistRatio)) {

                //Update instructions
                navStep++;
                stepCount = 0;
                CheckNavigation();

            }
        }

        //Right

        if(turnCode == 1) {

            //check if they completed the turn
            if(!turnComplete) {
                arrow.setImageDrawable(getDrawable(R.drawable.arrowforwardfill));
                arrow.setRotation(90);
                instructionView.setText("Turn Right Now");

                if((azimuth < azumuthStart - 90 && azumuthStart > 90) || (azimuth < azumuthStart + 270 && azimuth > 0 && azumuthStart < 90))  {
                    arrow.setImageDrawable(getDrawable(R.drawable.arrowforward));
                    arrow.setRotation(0);
                    instructionView.setText("Walk Forward " + targetDistance / 12 + " feet");
                    turnComplete = true;
                }
            }
            //now check if they completed the distance portion
            else if (stepCount >= (float) (targetDistance / stepDistRatio)) {

                //Update instructions
                navStep++;
                stepCount = 0;
                turnComplete = false;
                CheckNavigation();

            }
        }

        //Right

        if(turnCode == 3) {

            //check if they completed the turn
            if(!turnComplete) {
                arrow.setImageDrawable(getDrawable(R.drawable.arrowforwardfill));
                arrow.setRotation(270);
                instructionView.setText("Turn Left Now");

                if((azimuth > azumuthStart + 90 && azumuthStart < 90) || (azimuth > azumuthStart -270 && azimuth < 0 && azumuthStart > 90))  {

                    arrow.setImageDrawable(getDrawable(R.drawable.arrowforward));
                    arrow.setRotation(0);
                    instructionView.setText("Walk Forward " + targetDistance / 12 + " feet");
                    turnComplete = true;
                }
            }
            //now check if they completed the distance portion
            else if (stepCount >= (float) (targetDistance / stepDistRatio)) {

                //Update instructions
                navStep++;
                stepCount = 0;
                turnComplete = false;
                CheckNavigation();

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
            updateNav(directionArray[0], directionArray[1]);
        }
        else{
            finish();
        }

    }

    protected void updateNav(int distance, int tcode){
        //Update the global variables and the navarrow indicator
        targetDistance = distance;
        turnCode = tcode;
        azumuthStart = azimuth;

        //create the instruction string
        if(turnCode == 0){
            instructionView.setText("Walk Forward " + distance / 12 + " feet");
        }
        else if( turnCode == 3){
            instructionView.setText("Turn Left in "+ distance / 12 + " feet");

        }
        else if( turnCode == 1){
            instructionView.setText("Turn Right in "+ distance / 12 + " feet");

        }

       //we need to know what angle the phone was at the start so we know when they complete the turn
    }


    private void parseDetailedPath (String path) {
        int i = 0;
        int commaCount = 0;
        int tempDirection = 0, tempDistance = 0;
        String temp = path.replace("[", ""); temp = temp.replace("]", "");

        for (int j = 0; j < temp.length(); j++) {
            if (temp.substring(j,j+1).equals(",")) {
                commaCount++;
                if (commaCount == 1) {
                    tempDirection = Integer.parseInt(temp.substring(i,j));
                } else {
                    commaCount = 0;
                    tempDistance = Integer.parseInt(temp.substring(i,j));
                    int tempArray[] = new int[]{tempDistance, tempDirection};
                    directionList.add(tempArray);
                }
                i = j+1;
            }
        }


    }

}

