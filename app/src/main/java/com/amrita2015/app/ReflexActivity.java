package com.amrita2015.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;


public class ReflexActivity extends Activity {

    Animation animation;
    CountDownTimer countDownTimer;
    TextView report;
    private static String TAG = "Animation";
    private ArrayList<Integer> scores = new ArrayList<Integer>();
    private int nTimes = 0;
    private String name;
    private long testDate;
    private String testMode;
    private MySQLHelper dbhelper;
    private float finalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflex);

        animation = (Animation) findViewById(R.id.testReflex);
        report = (TextView) findViewById(R.id.meter);
        Intent previousIntent = this.getIntent();
        name = previousIntent.getStringExtra("userId");
        testDate = previousIntent.getLongExtra("testDate", 0);
        testMode = previousIntent.getStringExtra("testMode");
        dbhelper = MySQLHelper.getInstance(this);
        dbhelper.getWritableDatabase();

        saveAction();

        countDownTimer = new CountDownTimer(10000, 25) {
            public void onTick(long toFinish) {
                long timeLeft = toFinish/1000;
                animation.update();
                animation.invalidate();
                //Log.v(TAG, "timer left = " + timeLeft);




            }
            public void onFinish() {
                Log.v(TAG, "Timer Done");



            }
        };
        //countDownTimer.start();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void startAction(View v) {
        countDownTimer.start();
    }
    public void stopAction(View v) {
        countDownTimer.cancel();
        scores.add(animation.getScore());
        nTimes++;
        report.setText(" Score = " + animation.getScore());
        if(nTimes==3) {
            float average = 0;
            int len = scores.size();
            for(int i=0; i<scores.size(); i++) {
                average += scores.get(i);
            }
            average = (float)(average)/(float)len;
            NumberFormat formatter = NumberFormat.getNumberInstance();
            formatter.setMaximumFractionDigits(1);
            formatter.setMaximumFractionDigits(2);


            report.setText(" Score : " +  animation.getScore() + " Avg : " + formatter.format(average));
            finalScore = average;
        }

    }
    public void clearAction(View v) {
        nTimes=0;
        scores.clear();
        animation.reset();
        animation.invalidate();
        report.setText("Score : ");

    }

    private void saveAction() {
        Button b = (Button)this.findViewById(R.id.saveButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbhelper.addBrainData(name, testDate, testMode, finalScore);
            }
        });
    }
}
