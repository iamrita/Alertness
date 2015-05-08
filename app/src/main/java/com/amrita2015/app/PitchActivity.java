package com.amrita2015.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;


public class PitchActivity extends Activity {
    double[] data;
    private GraphView graphView;
    private LinearLayout layout;
    String testName;
    long testDate;
    String testMode;

    TextView maxText, avgText, highFreqText, powerText;
    float maxFreq, avgFreq, std;
    int numHigh;
    MySQLHelper dbStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch);
        Intent prev_intent = this.getIntent();
        data = prev_intent.getDoubleArrayExtra("data");
        maxFreq= (float)prev_intent.getDoubleExtra("max", 0);
        avgFreq = (float) prev_intent.getDoubleExtra("average", 0);
        std = (float)prev_intent.getDoubleExtra("std", 0);
        //power = (float)prev_intent.getDoubleExtra("power", 0);
        testName = prev_intent.getStringExtra("userId");
        testDate = prev_intent.getLongExtra("testDate", 0);
        testMode = prev_intent.getStringExtra("testMode");

        avgText = (TextView)findViewById(R.id.ave_pitch);
        maxText = (TextView) findViewById(R.id.max_pitch);
        highFreqText = (TextView) findViewById(R.id.num_high);
        powerText = (TextView) findViewById(R.id.power);

        maxText.setText("Highest Frequency : " + maxFreq);
        avgText.setText("Average Frequency : " + avgFreq);
        highFreqText.setText("Std Dev : " + std);
        //powerText.setText("Power at High Freqencies : " + power);


        GraphView.GraphViewData[] graphData = new GraphView.GraphViewData[data.length];
        for(int i=0; i< data.length; i++){
            GraphView.GraphViewData gdata = new GraphView.GraphViewData(i, data[i]);
            graphData[i] = gdata;

        }
        GraphViewSeries gSeries = new GraphViewSeries(graphData);
        graphView = new BarGraphView(this, "Volume Graph");
        graphView.addSeries(gSeries);
        graphView.setShowLegend(true);
        layout = (LinearLayout) findViewById(R.id.plot_volume);
        layout.addView(graphView);
    }

    public void saveAction(View view) {
        if(testName == null || testName.length() == 0) {
            return;
        }
        dbStore = MySQLHelper.getInstance(this);
        dbStore.getWritableDatabase();
       dbStore.addFreqData(testName, testDate, testMode, maxFreq, avgFreq, std);
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
}