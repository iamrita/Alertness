
package com.amrita2015.app;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class SpeechtoTextActivity extends Activity {

    protected static final int REQUEST_OK = 1;
    public static final String TAG = "Talk ";
    private String measure = "";
    private String[] splitmeasure;
    private float score;
    Button saveB;
    Button read;
    EditText userInput;
    MySQLHelper dbhelper;
    String name;
    String testMode;
    long testDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speechto_text);
        Intent prevIntent = this.getIntent();
        name = prevIntent.getStringExtra("userId");
        testMode = prevIntent.getStringExtra("testMode");
        testDate = prevIntent.getLongExtra("testDate", 0);
        Button talkButton = (Button) findViewById(R.id.button1);
        saveB = (Button)findViewById(R.id.save);
        read = (Button)findViewById(R.id.read);
        userInput = (EditText)findViewById(R.id.editText);
        measure = "the preliminary innovation of cinnamon has interesting specificity";
        ((TextView)findViewById(R.id.cannedSaying)).setText(measure);
        splitmeasure = measure.split(" ");
        dbhelper = MySQLHelper.getInstance(this);

        saveAction();
        readAction();



    }

    public void talkAction(View view) {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        try {
            startActivityForResult(i, REQUEST_OK);
        } catch (Exception e) {
            Log.v(TAG, "Exception in calling speech recognizer");
        }

    }

    public void saveAction() {

        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbhelper.getWritableDatabase();
                System.out.println("slur score = " + score);
                dbhelper.addSlurData(name, testDate, testMode,score );

                System.out.println("inside save action");

            }
        });
    }

    public void readAction() {
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbhelper.readVoiceData(userInput.getText().toString());
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_OK  && resultCode==RESULT_OK) {
            ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Log.v(TAG, thingsYouSaid.get(0));

            double strike = 0.0;
            String[] userSplit = thingsYouSaid.get(0).split(" ");
            for (int i = 0 ;i < userSplit.length; i ++ ){
                System.out.println("what i said " + userSplit[i]);

            }
            for (int i = 0 ;i < splitmeasure.length; i ++ ){
                System.out.println("canned: " + splitmeasure[i]);

            }

            if (userSplit.length == splitmeasure.length) {
                Log.v(TAG, "both length are same");
                int counter = 0;

                for (int i = 0; i < splitmeasure.length; i++) {
                    if (userSplit[i].equals(splitmeasure[i])) {
                        Log.v(TAG, userSplit[i] + " matched " +  splitmeasure[i]);


                        counter++;
                    } else {
                        Log.v(TAG, userSplit[i] + " NOT matched " +  splitmeasure[i]);
                    }
                }

                Log.v(TAG, "counter = " + counter);

                if (counter == (splitmeasure.length)) {
                    ((TextView)findViewById(R.id.resultText)).setText("ALL CORRECT: You are fit to drive!");
                    System.out.println("ALL CORRECT: you are fit to drive!");
                    Log.v(TAG, "ALL CORRECT: you are fit to drive!" );
                    score = 100f;
                }

            } else {
               int shorter = Math.min(splitmeasure.length, userSplit.length);
               float counter = 0;
               for (int i = 0; i < shorter; i++) {
                   if (userSplit[i].equals(splitmeasure[i])) {
                       Log.v(TAG, userSplit[i] + " matched " + splitmeasure[i]);
                       counter++;
                   }
               }

                ((TextView)findViewById(R.id.resultText)).setText("you got " + counter + " words right");
                System.out.println("you got " + counter + "words right");
                Log.v(TAG, "you got " + counter + "words right");


                float percentage = (counter/splitmeasure.length) * 100;
                score = (float)(Math.floor((percentage * 100)/100));
                System.out.println("your percentage is " + score);
                Log.v(TAG, "your percentage is " + score );

            }

            /*if (thingsYouSaid.get(0).equals(measure)) {
                System.out.println("yes! you got it!");
                ((TextView)findViewById(R.id.resultText)).setText("You are fit to drive!");
            } else {
                for (int i = 0 ; i < splitmeasure.length; i++) {
                    if (userSplit[i] != splitmeasure[i]) {
                        System.out.println("in here");
                        strike++;
                    }
                }


                System.out.println("you are unfit to drive");
                System.out.println("strikes " + strike);
                double subtract = splitmeasure.length-strike;
                System.out.println("lenth of split measure = " + splitmeasure.length);
                Double percentage = (subtract/splitmeasure.length) * 100;
                ((TextView)findViewById(R.id.resultText)).setText("You got it " + (Math.floor(percentage * 100) / 100) + "% right");
            }*/

            ((TextView)findViewById(R.id.text1)).setText(thingsYouSaid.get(0));
        }
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