package com.amrita2015.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by amritav on 2/5/15.
 */
public class MySQLHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Alert";
    private static final int DATABASE_VERSION = 1;
    private static MySQLHelper sInstance;

    private String brainTestTable = "create table BrainTest (id text not null, ts long not null, type text not null, score float not null)";
    private String balanceTestTable = "create table BalanceTest (id text not null, ts long not null, type text not null, score float not null)";
    private String volumeTestTable = "create table VolumeTest (id text not null, ts long null, avg float not null, max float not null, fade int not null)";
    private String voiceTestTable = "create table VoiceTest (id text not null, ts long not null, type text not null, maxAmp float not null, " +
            "avgAmp float not null, stdDev float not null, length int not null, gaps int not null )";
    private String freqTestTable = "create table FreqTest (id text not null, ts long not null, type text not null, maxFreq float not null, avgFreq float not null, stdDev float not null)";
    private String slurTestTable = "create table SlurTest (id text not null, ts long not null, type text not null, score float not null)";


    private MySQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static MySQLHelper getInstance(Context context)
    {
        if(sInstance == null) {
            sInstance = new MySQLHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(brainTestTable);
        sqLiteDatabase.execSQL(voiceTestTable);
        sqLiteDatabase.execSQL(balanceTestTable);
        sqLiteDatabase.execSQL(volumeTestTable);
        sqLiteDatabase.execSQL(freqTestTable);
        sqLiteDatabase.execSQL(slurTestTable);
        //execute tables db.execSQL
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    public void addBrainData(String id, long ts, String type, float score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put("id", id);
        vals.put("ts", ts);
        vals.put("type", type);
        vals.put("score", score);
        db.insert("BrainTest", null, vals );

    }

    public void addSlurData(String id, long ts, String type, float score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put("id", id);
        vals.put("ts", ts);
        vals.put("type", type);
        vals.put("score", score);
        db.insert("SlurTest", null, vals );

    }

    public void addVoiceData(String id, long ts, String type, float maxAmp, float avgAmp, float stdDev, int len, int gaps)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put("id", id);
        vals.put("ts", ts);
        vals.put("type", type);
        vals.put("maxAmp", maxAmp);
        vals.put("avgAmp", avgAmp);
        vals.put("stdDev", stdDev);
        vals.put("length", len);
        vals.put("gaps", gaps);
        db.insert("VoiceTest", null, vals );

    }


    public void addFreqData(String id, long ts, String type, float maxFreq, float avgFreq, float stdDev)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put("id", id);
        vals.put("ts", ts);
        vals.put("type", type);
        vals.put("maxFreq", maxFreq);
        vals.put("avgFreq", avgFreq);
        vals.put("stdDev", stdDev);
        db.insert("FreqTest", null, vals );

    }
    public void addBalanceData(String id, long ts, String type, float score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put("id", id);
        vals.put("ts", ts);
        vals.put("type", type);
        vals.put("score", score);
        db.insert("BalanceTest", null, vals);
    }

    public void addVolumeData(String id, long ts, float avg, float max, int fade) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put("id", id);
        vals.put("ts", ts);
        vals.put("avg", avg);
        vals.put("max", max);
        vals.put("fade", fade);
        db.insert("VolumeTest", null, vals);

    }

    public void recreateDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP TABLE IF EXISTS BrainTest";
        db.execSQL(query);
        query = "DROP TABLE IF EXISTS VoiceTest";
        db.execSQL(query);
        query = "DROP TABLE IF EXISTS BalanceTest";
        db.execSQL(query);
        query = "DROP TABLE IF EXISTS VolumeTest";
        db.execSQL(query);
        query = "DROP TABLE IF EXISTS FreqTest";
        db.execSQL(query);
        query = "DROP TABLE IF EXISTS SlurTest";
        db.execSQL(query);

        onCreate(db);
    }

    public void clearData() {
        getWritableDatabase().delete("BrainTest", null, null);
        getWritableDatabase().delete("VoiceTest", null, null);
        getWritableDatabase().delete("BalanceTest", null, null);
        getWritableDatabase().delete("VolumeTest", null, null);
    }


    public float readBalanceData(String id) {
        Cursor c;
        float score = 0f;
        try {

            String query = "select * from BalanceTest where id = '" + id + "'";
            c = getReadableDatabase().rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    score = c.getFloat(2);
                    System.out.println("your balance score is : " + score);

                } while (c.moveToNext());
            }
        } catch(Exception e) {

        }
        return score;

    }

    public float readBrainData(String id) {
        Cursor c;
        float score = 0f;
        try {

            String query = "select * from BrainTest where id = '" + id + "'";
            c = getReadableDatabase().rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    score = c.getFloat(3);
                    System.out.println("your score is : " + score);

                } while (c.moveToNext());
            }
        } catch(Exception e) {

        }
        return score;

    }

    public float readVoiceData(String id) {
        Cursor c;
        float score = 0f;
        try {

            String query = "select * from VoiceTest where id = '" + id + "'";
            c = getReadableDatabase().rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    score = c.getFloat(2);
                    System.out.println("your voice score is : " + score);

                } while (c.moveToNext());
            }
        } catch(Exception e) {

        }
        return score;

    }

    public void readVolumeData(String id) {
        Cursor c;
        float avg = 0f;
        float max = 0f;
        int fade=0;
        try {

            String query = "select * from VolumeTest where id = '" + id + "'";
            c = getReadableDatabase().rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    avg = c.getFloat(2);
                    max = c.getFloat(3);
                    fade = c.getInt(4);

                    System.out.println("your avg amp is : " + avg);
                    System.out.println("your max amp is :" + max);
                    System.out.println("your fade away is " + fade);


                } while (c.moveToNext());
            }
        } catch(Exception e) {

        }

    }

    public AlertData readData(String id, String mode) {

        Cursor c;

        AlertData data = null;


        try {
            String query = "select * from VoiceTest where id = '" + id +  "' and type = '" + mode + "'";

            Log.v("SQl", query);
            c = getReadableDatabase().rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    String nameId = c.getString(0);
                    long date = c.getLong(1);
                    String type = c.getString(2);
                    float maxAmp = c.getFloat(3);
                    float avgAmp = c.getFloat(4);
                    float std = c.getFloat(5);
                    int len = c.getInt(6);
                    int gaps = c.getInt(7);
                    if(data == null) {
                        data = new AlertData(nameId, date, type);

                    }
                    data.setMaxAmplitude(maxAmp);
                    data.setAvgAmplitude(avgAmp);
                    data.setStdAmplitude(std);
                    data.setAudioLen(len);
                    data.setAudioGaps(gaps);
                    //result.add(data);
                } while(c.moveToNext());
            }
            query = "select * from FreqTest where id = '" + id +  "' and type = '" + mode + "'";
            c = getReadableDatabase().rawQuery(query, null);

            if (c.moveToFirst()) {
                do {
                    String nameId = c.getString(0);
                    long date = c.getLong(1);
                    String type = c.getString(2);
                    float maxFreq = c.getFloat(3);
                    float avgFreq = c.getFloat(4);
                    float std = c.getFloat(5);


                    if(data == null) {
                        data = new AlertData(nameId, date, type);
                    }

                    data.setMaxFFreq(maxFreq);
                    data.setAvgFFReq(avgFreq);
                    data.setStdFFreq(std);

                    //result.add(data);
                } while (c.moveToNext());
            }
            query = "select * from BrainTest where id = '" + id +  "' and type = '" + mode + "'";
            c = getReadableDatabase().rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    String nameId = c.getString(0);
                    long date = c.getLong(1);
                    String type = c.getString(2);
                    float score = c.getFloat(3);

                    if (data == null) {
                        data = new AlertData(nameId, date, type);
                    }
                    data.setBrainScore(score);



                } while (c.moveToNext());
            }

            query = "select * from BalanceTest where id = '" + id +  "' and type = '" + mode + "'";
            c = getReadableDatabase().rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    String nameId = c.getString(0);
                    long date = c.getLong(1);
                    String type = c.getString(2);
                    float score = c.getFloat(3);

                    if (data == null) {
                        data = new AlertData(nameId, date, type);

                    }
                    data.setBalanceScore(score);

                }while (c.moveToNext());
            }

            query = "select * from SlurTest where id = '" + id +  "' and type = '" + mode + "'";
            c = getReadableDatabase().rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    System.out.println("inside reading slur");

                    String nameId = c.getString(0);
                    long date = c.getLong(1);
                    String type = c.getString(2);
                    float score = c.getFloat(3);
                    System.out.println("your slur score is " + score);
                    if (data == null) {
                        data = new AlertData(nameId, date, type);
                    }
                    data.setSlurScore(score);

                } while (c.moveToNext());
            }





        } catch(SQLiteConstraintException e) {
            System.out.println("CONSTRAINT EXCEPTION!!!");
            Log.v("SQL", "constraint exception");
        } catch(Exception e) {
            Log.v("SQL", "Exception in SQL Lite");

            e.printStackTrace();
        }

        return data;

    }


}

