package com.amrita2015.app;

import com.amrita2015.app.TextCaptcha.TextOptions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class CaptchaActivity extends Activity{
    ImageView myImage;
    Button button;
    Button button2;
    Button saveData;
    Button readInfo;
    EditText userIn;
    EditText userenters;
    TextCaptcha captcha;
    TextView scores;
    String userEntry = "";
    float x = 0;
    float y = 0;
    int tries = 0;
    float captchascore;
    MySQLHelper dbhelper;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha);
        Intent prev_intent = this.getIntent();


        myImage = (ImageView)findViewById(R.id.imageView1);
        button = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.refreshbutton);
        saveData = (Button)findViewById(R.id.saveButton);
        userenters = (EditText)findViewById(R.id.enterID);
        readInfo = (Button)findViewById(R.id.read);

        userIn = (EditText)findViewById(R.id.userInput);
        scores = (TextView)findViewById(R.id.scoreCard);

        captcha = new TextCaptcha(1000, 300, 7, TextOptions.NUMBERS_AND_LETTERS);

        myImage.setImageBitmap(captcha.image);
      //  LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(captcha.getWidth(), captcha.getHeight());
       // myImage.setLayoutParams(params);
        //text.setText(c.answer);
        this.submitButton();
        this.refresh();

        saveAction();
        readAction();



    }

    public boolean onTouchEvent(MotionEvent e) {
        x = e.getX();
        y = e.getY();
        return true;
    }

    public void refresh() {
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tries = 0;
                captcha = new TextCaptcha(1000, 300, 7, TextOptions.NUMBERS_AND_LETTERS);

                myImage.setImageBitmap(captcha.image);
            }
        });

    }

    public void readAction() {
        readInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbhelper.readBrainData(userenters.getText().toString());
            }
        });
    }

    public void saveAction() {
        dbhelper = MySQLHelper.getInstance(this);
        saveData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                dbhelper.getWritableDatabase();
                dbhelper.addBrainData(userenters.getText().toString(), System.currentTimeMillis(), "test", captchascore);

            }
        });

    }


    public void submitButton() {

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                userEntry = userIn.getText().toString();
                Log.v("the user entered in " , userEntry);
                String temp = "";
                for (int i = 0; i < captcha.getLetters().length; i++) {
                    temp += captcha.getLetters()[i];
                }

                Log.v("Captcha says: ", temp);
                char[] captchaLetters = captcha.getLetters();
                char[] userLetters = userEntry.toCharArray();
                if (userEntry.equals(temp)) {
                    scores.setTextColor(Color.GREEN);
                    scores.setTextSize(100);
                    scores.setText("Correct!");
                    captchascore = 100f;

                } else {

                    scores.setTextColor(Color.RED);
                    scores.setTextSize(20);
                    double strike = 0.0;

                    if (captchaLetters.length > userLetters.length) {
                        scores.setText("You entered in too few characters.");
                    } else {
                        for (int i = 0 ; i < captchaLetters.length; i++) {
                            if (userLetters[i] != captchaLetters[i]) {
                                System.out.println("in here");
                                strike++;
                            }
                        }

                    }
                    double subtract = captchaLetters.length-strike;
                   Double percentage = (subtract/captchaLetters.length) * 100;
                    scores.setText("You got it " + (Math.floor(percentage * 100) / 100) + "% right");
                    captchascore = (float)(Math.floor(percentage * 100)/100);
                    //scores.setText("Try Again!");
                    tries++;
                }
                if (tries > 3) {
                    Toast.makeText(getApplicationContext(), "You are not fit to drive", Toast.LENGTH_LONG);
                }

            }
        });

    }










}



