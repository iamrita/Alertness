package com.amrita2015.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;


public class BalanceActivity extends Activity {
    private DrawCompass myCompass;
    private DrawShaking myshakes;
    private static SensorManager sensorService;
    private Sensor sensor;
    private TextView tv;
    private TextView baselinetv;
    private TextView strike1;
    private ArrayList<Float> calibrations;
    private ArrayList<Float> diffs;
    private ArrayList<Float> diffsRoll;
    private ArrayList<Float> diffsPitch;
    private ArrayList<Float> baselineY;
    private ArrayList<Float> baselineZ;
    private ArrayList<Float> averageCalc;

    private ArrayList<Float> diff0;
    private ArrayList<Float> diff1;
    private ArrayList<Float> diff2;

    private float baseline;
    private float baselineR;
    private float baselineP;
    private Button check;
    private Button readData;
    private Button saveData;
    private EditText userinfo;
    private float reading;
    private float roll;
    private float pitch;

    private float avgReading;
    MySQLHelper dbhelper;
    private TextView averageDisplay;
    private String name;
    private long testDate;
    private String testMode;
    private boolean walk=false;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        Intent previousIntent = this.getIntent();
        name = previousIntent.getStringExtra("userId");
        testDate = previousIntent.getLongExtra("testDate", 0);
        testMode = previousIntent.getStringExtra("testMode");
        myCompass = (DrawCompass)findViewById(R.id.compass);
        myshakes = (DrawShaking)findViewById(R.id.shaking);
        check = (Button)this.findViewById(R.id.testButton);
        saveData = (Button)this.findViewById(R.id.save);
        readData = (Button)this.findViewById(R.id.read);
        averageDisplay = (TextView)this.findViewById(R.id.textView3);
        tv = (TextView)findViewById(R.id.textView);
        calibrations = new ArrayList<Float>();
        diffs = new ArrayList<Float>();
        averageCalc = new ArrayList<Float>();
        baselineY = new ArrayList<Float>();
        baselineZ = new ArrayList<Float>();
        diffsRoll = new ArrayList<Float>();
        diffsPitch = new ArrayList<Float>();
        diff0 = new ArrayList<Float>();
        diff1 = new ArrayList<Float>();
        diff2 = new ArrayList<Float>();
        baseline = 0;
        //baselinetv = (TextView)findViewById(R.id.baselineText);
        //strike1 = (TextView)findViewById(R.id.strike1);
       // myCompass.invalidate();

        if (myCompass == null) {
            Log.v("mycompass object ", "is null");
        }

        sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorService.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        startAction();
        stopAction();
        calibrateAction();
        checkAction();
        dbhelper = MySQLHelper.getInstance(this);

//        dbhelper.recreateDB();
        saveAction();
        

    }

    public float getReading() {
        return reading;
    }

    public void setReading(float f, float r, float p) {
        reading = f;
        roll = r;
        pitch = p;

    }



    public void startAction() {
        Button start = (Button)this.findViewById(R.id.startButton);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (sensor != null) {
                    sensorService.registerListener(mySensorEventListener, sensor,
                            SensorManager.SENSOR_DELAY_NORMAL);
                    calibrations.clear();
                    baselineY.clear();
                    baselineZ.clear();
                    Log.i("Compass MainActivity", "Registerered for ORIENTATION Sensor");
                } else {
                    Log.e("Compass MainActivity", "Registerered for ORIENTATION Sensor");
                    finish();
                }
            }
        });

    }

    public void saveAction() {

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("saving");


                dbhelper.getWritableDatabase();
                dbhelper.addBalanceData(name, testDate, testMode,avgReading);
                //AlertData data = dbhelper.readData(name, testMode);
                //Log.v("BalanceActivity", data.toString());


            }
        });
    }

    public void readAction() {
        readData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("reading");

                dbhelper.readBalanceData(userinfo.getText().toString());
            }
        });

    }

    public void stopAction() {
        Button stop = (Button)this.findViewById(R.id.stopButton);
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sensorService.unregisterListener(mySensorEventListener);
            }
        });
    }

    public void calibrateAction() {
        Button calibrate = (Button)this.findViewById(R.id.calibrateButton);
        calibrate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                float sum = 0;
                for (int i = 0; i < calibrations.size(); i++) {
                    sum = sum + calibrations.get(i);
                }
                baseline = calibrations.get(calibrations.size()-1);
                baselineR = baselineY.get(baselineY.size()-1);
                baselineP = baselineZ.get(baselineZ.size()-1);
                NumberFormat formatter = NumberFormat.getNumberInstance();
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(2);
                //baselinetv.setText("A: " + formatter.format(baseline) + "  R: " + formatter.format(baselineR) + "  P: " +
                 //       formatter.format(baselineP));

            }
        });
    }




   public void checkAction() {


        check.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sensorService.registerListener(mySensorEventListener, sensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
                walk = true;
                diffs.clear();
                diffsRoll.clear();
                diffsPitch.clear();
                averageCalc.clear();
                diff0.clear();
                diff1.clear();
                diff2.clear();

                new CountDownTimer(10000, 1000) {


                    public void onTick(long millisUntilFinished) {

                           Log.v("in the loop", "Yup");

                           diffs.add(reading-baseline);
                           diffsRoll.add(Math.abs(roll-baselineR));
                           diffsPitch.add(Math.abs(pitch-baselineP));
                           averageCalc.add(Math.abs(reading-baseline));
                           Log.v("here are: ", diffs.toString());


                    }

                    public void onFinish() {
                        sensorService.unregisterListener(mySensorEventListener);
                        walk=false;
                        check.setBackgroundColor(Color.CYAN);
                        myshakes.setPath(diffs);
                        float sum = 0;
                        for (int i = 0; i < averageCalc.size(); i++) {
                            sum = sum + averageCalc.get(i);
                        }
                        float sum2 = 0;
                        for (int i = 0; i < diffsRoll.size(); i++) {
                            sum2 = sum2 + diffsRoll.get(i);
                        }
                        float averageRoll = sum2/(diffsRoll.size());
                        float sum3 = 0;
                        for (int i = 0; i < diffsPitch.size(); i++) {
                            sum3 = sum3 + diffsPitch.get(i);
                        }
                        float averagePitch = sum3/(diffsPitch.size());
                        System.out.println("your average diff is " + sum/averageCalc.size() );
                        avgReading = sum/averageCalc.size();
                        //averageDisplay.setText("azimuth :   " + avgReading + " pitch :  " + averageRoll + " roll :  " + averagePitch);

                        float avgAz = 0;
                        for(int i=0; i<diff0.size(); i++) {
                            avgAz += diff0.get(i);
                        }
                        avgAz = avgAz/diff0.size();

                        float avgRoll = 0;
                        for(int i=0; i<diff1.size(); i++) {
                            avgRoll += diff1.get(i);
                        }
                        avgRoll = avgRoll/diff1.size();

                        float avgPitch = 0;
                        for(int i=0; i<diff2.size(); i++) {
                            avgPitch += diff2.get(i);
                        }
                        avgPitch = avgPitch/diff2.size();


                        NumberFormat formatter = NumberFormat.getNumberInstance();
                        formatter.setMinimumFractionDigits(2);
                        formatter.setMaximumFractionDigits(2);

                        averageDisplay.setText("azimuth : " + formatter.format(avgAz) + " pitch : " + formatter.format(avgRoll) + " roll : " +
                                formatter.format(avgPitch));

                        }
                }.start();

        }
    });
    }

    private SensorEventListener mySensorEventListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // angle between the magnetic north direction
            // 0=North, 90=East, 180=South, 270=West
            Log.v("getdirection", "onSensorChanged");
            float azimuth = event.values[0];
            float roll = event.values[1];
            float pitch = event.values[2];
            myCompass.updateData(azimuth);

            if(!walk) {
                calibrations.add(azimuth);
                baselineY.add(roll);
                baselineZ.add(pitch);
            } else {
                float d0 = Math.abs(baseline - azimuth);
                float d1 = Math.abs(baselineR - roll);
                float d2 = Math.abs(baselineP - pitch);
                diff0.add(d0);
                diff1.add(d1);
                diff2.add(d2);
            }
            setReading(azimuth, roll, pitch);
            tv.setText("Delta: " + azimuth);
            Log.v("azimuth value is " , azimuth +"");

        }
    };




}
