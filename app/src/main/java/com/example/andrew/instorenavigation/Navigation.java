package com.example.andrew.instorenavigation;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

public class Navigation extends Activity implements SensorEventListener {


    //declare the sensor manager
    private SensorManager sensorManager;
    private Sensor mAccel,mMag, mstep, mla;
    private int stepCount, stepSense;
    private Date lastUpdate;
    private float lastZ, newZ, avgZ, count,zSum, newX, newY, lastx, lasty, mag1Value, mag2Value, mag3Value, stepCountReal;
    public static float[] mAccelerometer = null;
    public static float[] mGeomagnetic = null;

    /*

    set this to change the

    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //instatiate the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

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
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            //there is a step counter

            mstep = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        } else {


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
        zSum = 0;
        avgZ = 0;
        stepSense = 3;
        //get the time, this is used to prevent counting a single step multiple times
        lastUpdate = Calendar.getInstance().getTime();



    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, mAccel , SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mMag, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mstep, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mla, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        /*if(event.sensor == mAccel) {

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

                    stepCount++;

                    lastUpdate = Calendar.getInstance().getTime();
                }
            } else {

            }

            //get the average z
            count++;
            zSum += newZ;
            avgZ = zSum / count;

            //assign value to lastxyz before moving on
            lastZ = newZ;
            lastx = newX;
            lasty = newY;
        }
        */
        if (event.sensor == mMag) {

            //assign the new event values to the magnetometer array for use with finding direction
            mGeomagnetic = event.values;

            //get the magnetic field value
            mag1Value = event.values[0];
            mag2Value = event.values[1];
            mag3Value = event.values[2];

        }

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
                    stepDetected.setVisibility(View.VISIBLE);
                    stepCount++;
                    stepCountView.setText(Integer.toString(stepCount));
                    lastUpdate = Calendar.getInstance().getTime();
                }
            } else {
                stepDetected.setVisibility(View.INVISIBLE);
            }

            //report other readings
            //if (newX > lastx + 0.1 || newX < lastx - 0.1)
            x.setText(Float.toString(event.values[0]));
            //if (newY > lasty + 0.1 || newY < lasty - 0.1)
            y.setText(Float.toString(event.values[1]));
            //if (newZ > lastZ + 0.1 || newZ < lastZ - 0.1)
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

        if (event.sensor == mstep) {

            stepCountReal++;
        }

        /*

        TEST CODE FOR GETTING DIRECTIONS USING THE MAGNETIC FIELD SENSOR

         */

        double azimuth = 0;
        double pitch = 0;
        double roll = 0;

        if (mAccelerometer != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mAccelerometer, mGeomagnetic);

            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // at this point, orientation contains the azimuth(direction), pitch and roll values.
                azimuth = 180 * orientation[0] / Math.PI;
                pitch = 180 * orientation[1] / Math.PI;
                roll = 180 * orientation[2] / Math.PI;
            }

            /*

            //set the textview with the updated information
            mag1.setText(Double.toString(round(azimuth,2)));
            mag2.setText(Double.toString(round(pitch,2)));
            mag3.setText(Double.toString(round(roll,2)));

            //rotate the arrow
            arrow.setRotation((float)azimuth);

            */

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




}
