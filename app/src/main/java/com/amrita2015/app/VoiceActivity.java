package com.amrita2015.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;

public class VoiceActivity extends ActionBarActivity {

    private String name;
    private String testMode;
    private long testDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        Intent previousIntent = this.getIntent();
        name = previousIntent.getStringExtra("userId");
        testDate = previousIntent.getLongExtra("testDate", 0);
        testMode = previousIntent.getStringExtra("testMode");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        gotoSlurring();
        gotoVolume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.voice, menu);
        return true;
    }

    private void gotoVolume() {
        Button volbutton = (Button)this.findViewById(R.id.volumebutton);
        final Intent intent = new Intent(VoiceActivity.this, VolumeActivity.class);

        volbutton.setOnClickListener(

                new View.OnClickListener() {
                    public void onClick(View v) {
                        intent.putExtra("userId", name);
                        intent.putExtra("testDate", testDate);
                        intent.putExtra("testMode", testMode);
                        startActivity(intent);
                    }
                });
    }


    private void gotoSlurring() {
        Button slurbutton = (Button)this.findViewById(R.id.slurbutton);
        final Intent intent = new Intent(VoiceActivity.this, SpeechtoTextActivity.class);
        slurbutton.setOnClickListener(

                new View.OnClickListener() {
                    public void onClick(View v) {
                        intent.putExtra("userId", name);
                        intent.putExtra("testDate", testDate);
                        intent.putExtra("testMode", testMode);
                        startActivity(intent);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_voice, container, false);
            return rootView;
        }
    }

}
