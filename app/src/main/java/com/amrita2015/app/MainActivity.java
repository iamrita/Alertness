package com.amrita2015.app;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends Activity {
    private String name;
    private String testMode;
    private long testDate;
    private EditText enteredName;
    private MySQLHelper helper;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testDate = System.currentTimeMillis();
        enteredName = (EditText)this.findViewById(R.id.personame);
        helper = MySQLHelper.getInstance(this);
        //helper.recreateDB();


        helper.getWritableDatabase();



        this.gotoCaptcha();
        this.gotoVoice();
        this.gotoBalance();
       // this.transfer();
        //this.read();
        captcha();

    }


    private void captcha() {
        Button capbutton = (Button)this.findViewById(R.id.capbutton);
        capbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CaptchaActivity.class));
            }
        });
    }
    private void gotoVoice() {
        Button voicebutton = (Button)this.findViewById(R.id.mic_button);
      final Intent intent = new Intent(MainActivity.this, VoiceActivity.class);
        voicebutton.setOnClickListener(

                new View.OnClickListener() {
            public void onClick(View v) {
                intent.putExtra("userId", enteredName.getText().toString());
                intent.putExtra("testDate", testDate);
                intent.putExtra("testMode", testMode);

                startActivity(intent);
            }
        });
    }

    private void gotoBalance() {
        Button balancebutton = (Button)this.findViewById(R.id.balance_button);
       final Intent intent = new Intent(MainActivity.this, BalanceActivity.class);
        balancebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent.putExtra("userId", enteredName.getText().toString());
                intent.putExtra("testDate", testDate);
                intent.putExtra("testMode", testMode);
                startActivity(intent);
            }
        });
    }


    private void gotoCaptcha() {
        Button captchaButton = (Button)this.findViewById(R.id.human_brain_icon);
       final Intent intent = new Intent(MainActivity.this, ReflexActivity.class);
        captchaButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent.putExtra("userId", enteredName.getText().toString());
                intent.putExtra("testDate", testDate);
                intent.putExtra("testMode", testMode);
                startActivity(intent);
            }
        });

    }

    private void transfer() {
        RadioButton base = (RadioButton)this.findViewById(R.id.baseline);
        base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("baseline clicked");
                testMode = "baseline";
            }
        });

        RadioButton tester = (RadioButton)this.findViewById(R.id.test);
        tester.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                System.out.println("tester clicked");
                testMode = "test";
            }
        });
    }

    public void rbuttonClicked(View v) {
        boolean checked = ((RadioButton)v).isChecked();

        switch(v.getId()) {
            case R.id.baseline:
                if (checked) {
                    testMode = "baseline";
                }
                break;
            case R.id.test:
                if (checked) {
                    testMode = "test";
                }
                break;
        }
    }

    /*private void read() {
        Button b = (Button)this.findViewById(R.id.readData);
       final Intent intent = new Intent(this, DisplayData.class);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String testName = enteredName.getText().toString();


                AlertData base = helper.readData(testName, testMode);
                //AlertData test = helper.readData(testName, "test");



                String str = base.getId() + " : " + base.getType() + "\n" + base.toString();

                intent.putExtra("data", str);
                startActivity(intent);



            }
        });
    }*/
    public void readAction(View view ) {

        Intent intent = new Intent(this, DisplayData.class);
        String testName = enteredName.getText().toString();


        AlertData base = helper.readData(testName, testMode);
        //AlertData test = helper.readData(testName, "test");

        String str = base.getId() + " : " + base.getType() + "\n" + base.toString();

        intent.putExtra("data", str);
        startActivity(intent);

    }


    public void resultAction(View view) {
        TextView resultText = (TextView) findViewById(R.id.resultBox);


        String testName = enteredName.getText().toString();
        if(testName.isEmpty()) {
            resultText.setText("Please select a test name");
        }

        AlertData baseline = helper.readData(testName, "baseline");
        AlertData test = helper.readData(testName, "test");

        float brainDiff = 0;
        float balanceDiff = 0;
        float avgAmpDiff = 0;
        float slurDiff = 0;
        float avgFreqDiff = 0;


        if(baseline.getBrainScore()  !=  0)
            brainDiff = 100 * Math.abs(baseline.getBrainScore() - test.getBrainScore())/baseline.getBrainScore();
        if(baseline.getBalanceScore() != 0)
            balanceDiff = 100 * Math.abs(baseline.getBalanceScore() - test.getBalanceScore())/baseline.getBalanceScore();
        if(baseline.getSlurScore() != 0)
            slurDiff = 100 * Math.abs(baseline.getSlurScore() - test.getSlurScore())/baseline.getSlurScore();
        if(baseline.getAvgAmp() != 0)
            avgAmpDiff = 100 * Math.abs(baseline.getAvgAmp() - test.getAvgAmp())/baseline.getAvgAmp();
        avgFreqDiff = 100 *  Math.abs(baseline.getBrainScore() - test.getBrainScore())/baseline.getBrainScore();
        if(brainDiff > 10 || balanceDiff > 10 || slurDiff > 10 || avgAmpDiff > 30 ) {
            resultText.setText("OK to drive? NO");
            Log.v("results" , "brainDiff : " + brainDiff + " balanceDiff :  " + balanceDiff + " slurDiff : " + slurDiff + " avgAmpDiff : "
                    + avgAmpDiff);

        } else {
            resultText.setText("OK to drive? YES!");
        }



    }


}
