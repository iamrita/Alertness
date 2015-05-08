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

import java.text.NumberFormat;


public class GraphActivity extends Activity {

    double[] data;
    private GraphView graphView;
    private LinearLayout layout;
    double[] frequencies;
    float stdDev;
    int freqCount;
    float avgAmplitude;
    float maxAmplitude;
    int startIndex, stopIndex, audioLen, gaps;
    String name;
    long testDate;
    String testMode;

    MySQLHelper dbStore;


    TextView freqCountLabel, sdLabel, maxFreqLabel, avgAmpLabel, maxAmpLabel, fadeAwayLabel;
    TextView startIndexLabel, gapLabel, stdDevLabel, audioLenLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Intent prev_intent = this.getIntent();
        data = prev_intent.getDoubleArrayExtra("data");
        //frequencies = prev_intent.getDoubleArrayExtra("farray");
        //stdDev = prev_intent.getDoubleExtra("stdDev", 0);
        name = prev_intent.getStringExtra("userId");
        testMode = prev_intent.getStringExtra("testMode");
        avgAmplitude = (float)prev_intent.getDoubleExtra("avgValue", 0);
        maxAmplitude = (float)prev_intent.getDoubleExtra("maxValue", 0);
        stopIndex = prev_intent.getIntExtra("fadeIndex", 0);
        startIndex = prev_intent.getIntExtra("startIndex", 0);
        audioLen = prev_intent.getIntExtra("audioLen", 0);
        gaps = prev_intent.getIntExtra("gaps", 0);
        stdDev = (float)prev_intent.getDoubleExtra("stdDev", 0);



        testDate = prev_intent.getLongExtra("testDate", 0);

        //freqCount = prev_intent.getIntExtra("freqCount", 0");
        //fundaFreq = prev_intent.getDoubleExtra("fundaFreq", 0);


        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(4);
        formatter.setMaximumFractionDigits(4);

        avgAmpLabel = (TextView) findViewById(R.id.avgValue);
        maxAmpLabel = (TextView) findViewById(R.id.maxValue);
        fadeAwayLabel = (TextView) findViewById(R.id.fadeIndex);
        startIndexLabel = (TextView)findViewById(R.id.startIndex);
        stdDevLabel = (TextView)findViewById(R.id.stdDev);
        audioLenLabel = (TextView)findViewById(R.id.audioLen);
        gapLabel = (TextView)findViewById(R.id.gaps);


        //freqCountLabel = (TextView) findViewById(R.id.freqCount);
        //maxFreqLabel = (TextView) findViewById(R.id.highFreq);
        //sdLabel = (TextView) findViewById(R.id.stdDev);

        avgAmpLabel.setText(" Average : " +formatter.format(avgAmplitude));
        maxAmpLabel.setText(" Max : " + formatter.format(maxAmplitude));
        stdDevLabel.setText(" Std Dev : " + stdDev);

        fadeAwayLabel.setText(" FadeIndex : " + stopIndex);
        startIndexLabel.setText(" StartIndex : " + startIndex);
        gapLabel.setText(" Gaps : " + gaps);
        audioLenLabel.setText(" Audio Len : " + audioLen);
        //freqCountLabel.setText("Fundamental Frequency  : " + fundaFreq );
        //maxFreqLabel.setText("Highest Frequency : " + frequencies[frequencies.length -1]);
        //sdLabel.setText("Std Deviation : " + stdDev);


        GraphView.GraphViewData[] graphData = new GraphView.GraphViewData[data.length];
        for(int i=0; i< data.length; i++){
            GraphView.GraphViewData gdata = new GraphView.GraphViewData(i, data[i]);
            graphData[i] = gdata;

        }

        GraphViewSeries gSeries = new GraphViewSeries(graphData);
        graphView = new BarGraphView(this, "Voice Graph");
        graphView.addSeries(gSeries);
        graphView.setShowLegend(true);
        layout = (LinearLayout) findViewById(R.id.plot_graph);
        layout.addView(graphView);



    }

    public void saveAction(View view) {

        dbStore = MySQLHelper.getInstance(this);
        dbStore.getWritableDatabase();
        dbStore.addVoiceData(name, testDate, testMode, maxAmplitude, avgAmplitude, stdDev, audioLen, gaps);
        //dbStore.addVoiceData(testId, testDate, "test", maxAmplitude, avgAmplitude, stopIndex);

    }

    public void backAction(View view) {
        Intent intent = new Intent(this, VolumeActivity.class);
        this.startActivity(intent);

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
