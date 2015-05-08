package com.amrita2015.app;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jtransforms.fft.DoubleFFT_1D;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.ArrayList;



public class VolumeActivity extends Activity {

    private static final String TAG = "VoiceRecord";
    private String name;
    private String testMode;
    private long testDate;

    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS_IN = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_CHANNELS_OUT = AudioFormat.CHANNEL_OUT_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;

    // Initialize minimum buffer size in bytes.
    private int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS_IN, RECORDER_AUDIO_ENCODING);

    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private volatile boolean isRecording = false;
    double[] voiceData;

    ArrayList<Double> dArrayList = new ArrayList<Double>();

    CountDownTimer countDownTimer;
    CountDownTimer baselineTimer;
    TextView timer;
    //FLACStreamEncoder encoder;
    double maxAmp;
    int freqCount;
    ArrayList<Double> frequencies;
    double stdDev;
    DoubleFFT_1D jFFT;
    double fFreq;
    ArrayList<Double> magnitudes = new ArrayList<Double>();
    ArrayList<Double> pitch = new ArrayList<Double>();
    ArrayList<Double> baselines = new ArrayList<Double>();
    Button save;
    Button read;
    EditText user;
    double noiseBaseline = 0;

    float avgMagnitude = 0;
    float maxMagnitude = 0;
    float soundDies = 0;
    MySQLHelper dbhelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume);
        ((Button) findViewById(R.id.start_button)).setOnClickListener(btnClick);
        //((Button) findViewById(R.id.stop_button)).setOnClickListener(btnClick);

        Intent previousIntent = this.getIntent();
        name = previousIntent.getStringExtra("userId");
        testDate = previousIntent.getLongExtra("testDate", 0);
        testMode = previousIntent.getStringExtra("testMode");

        // = (Button)findViewById(R.id.savebutton);
        //read = (Button)findViewById(R.id.readbutton);
        user = (EditText)findViewById(R.id.editText);
        timer = (TextView) findViewById(R.id.timerBar);
        Log.v(TAG, "onCreate");
        enableButtons(false);

        if( bufferSize == AudioRecord.ERROR_BAD_VALUE)
            Log.e(TAG, "Bad Value for \"bufferSize\", recording parameters are not supported by the hardware");

        if( bufferSize == AudioRecord.ERROR )
            Log.e( TAG, "Bad Value for \"bufferSize\", implementation was unable to query the hardware for its output properties");

        Log.e( TAG, "\"bufferSize\"="+bufferSize);

        // Initialize Audio Recorder.
        recorder = new AudioRecord(AUDIO_SOURCE, RECORDER_SAMPLERATE, RECORDER_CHANNELS_IN, RECORDER_AUDIO_ENCODING, bufferSize);
        // Starts recording from the AudioRecord instance.
        if(recorder == null) {
            Log.v(TAG, "recorder is null");

        }

        countDownTimer = new CountDownTimer(5000, 1000) {
            public void onTick(long toFinish) {
                long timeLeft = toFinish/1000;
                //Log.v(TAG, "timer left = " + timeLeft);

                timer.setText("" + timeLeft);


            }
            public void onFinish() {
                //Log.v(TAG, "Done");
                timer.setText("Done");
                try {
                    stopRecording();
                } catch(Exception e) {
                    Log.v(TAG, "Exception in stopping");
                }
                enableButtons(false);


            }
        };
        baselineTimer = new CountDownTimer(5000, 1000) {

            public void onTick(long toFinish){

                long timeLeft = toFinish/1000;
                timer.setText("" + timeLeft);

            }

            public void onFinish() {
                timer.setText("Done");
                isRecording = false;
                if (null != recorder) {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                    recordingThread = null;
                }
                //Find the highest peak in the baselines
                double highest = baselines.get(0);
                for(int i=1; i< baselines.size(); i++) {
                    if(highest < baselines.get(i)) {
                        highest = baselines.get(i);
                    }
                }
                noiseBaseline = highest;
                baselines.clear();
                TextView baselineBar = (TextView) findViewById(R.id.baselineBar);
                NumberFormat formatter = NumberFormat.getNumberInstance();
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(2);
                baselineBar.setText(""+formatter.format(noiseBaseline));
                Log.v(TAG, "noiseBaseline = " + noiseBaseline);

            }
        };
        dbhelper = MySQLHelper.getInstance(this);

        //saveAction();
        //readAction();




    }

    /*private void saveAction() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double maxAmp = magnitudes.get(0);
                double avgAmp = magnitudes.get(0);

                for(int i=1; i<magnitudes.size(); i++) {
                    if(maxAmp < magnitudes.get(i)) {
                        maxAmp = magnitudes.get(i);
                        avgAmp += magnitudes.get(i);
                    }
                }
                int fadeIndex=0;
                for(int i=magnitudes.size()-1; i>=0; i--){
                    if(magnitudes.get(i) > noiseBaseline){
                        break;
                    }
                    fadeIndex++;
                }
                maxMagnitude = (float) maxAmp;
                avgMagnitude = (float) avgAmp;
                System.out.println("avgMagnitude = " + avgMagnitude);
                dbhelper.getWritableDatabase();
                dbhelper.addVolumeData(user.getText().toString(), System.currentTimeMillis(), avgMagnitude, maxMagnitude, fadeIndex);
            }
        });
    }*/

    private void readAction() {
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbhelper.readVolumeData(user.getText().toString());
            }
        });
    }

    private void enableButton(int id, boolean isEnable) {
        ((Button) findViewById(id)).setEnabled(isEnable);
    }

    private void enableButtons(boolean isRecording) {
        enableButton(R.id.start_button, !isRecording);
        //enableButton(R.id.stop_button, isRecording);
    }

    private void startRecording() {
        if( bufferSize == AudioRecord.ERROR_BAD_VALUE)
            Log.e(TAG, "Bad Value for \"bufferSize\", recording parameters are not supported by the hardware");

        if( bufferSize == AudioRecord.ERROR )
            Log.e( TAG, "Bad Value for \"bufferSize\", implementation was unable to query the hardware for its output properties");

        Log.e( TAG, "\"bufferSize\"="+bufferSize);

        // Initialize Audio Recorder.
        recorder = new AudioRecord(AUDIO_SOURCE, RECORDER_SAMPLERATE, RECORDER_CHANNELS_IN, RECORDER_AUDIO_ENCODING, bufferSize);
        // Starts recording from the AudioRecord instance.
        if(recorder == null) {
            Log.v(TAG, "recorder is null");

        }
        recorder.startRecording();
        countDownTimer.start();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }



    private void computeSlidingFFT(float[] input){

        int len = input.length;

        //Log.v(TAG, "pitch : bufferSize = " + bufferSize);
        //Log.v(TAG, "pitch : len = " + len);

        double[] data = new double[len*2];
        jFFT = new DoubleFFT_1D(len);
        double[] magnitude = new double[len];


        for(int i=0; i < input.length; i++) {

            data[i*2] = input[i]/32768.0;
            data[(i*2) + 1] = 0.0;
            //Log.v(TAG, ""+ input[i]);
        }
        jFFT.complexForward(data); //gives you back an array and puts it into a bin amd maps to a frequency

        for(int i=0; i<len; i++) {

            double real = data[i*2];
            double img = data[(i*2) +1];
            magnitude[i] = Math.sqrt(real*real + img*img);
        }

        double peak = 0;
        int index=0;
        for(int i=0; i< len/2; i++) {
            if(peak < magnitude[i]) {
                peak = magnitude[i];
                index = i;
            }
        }
        //magnitudes.add(peak);


        double freq = RECORDER_SAMPLERATE * index/bufferSize;

        pitch.add(freq);



    }

    private void computeBaselineFFT(float[] input){

        int len = input.length;

        double[] data = new double[len*2];
        jFFT = new DoubleFFT_1D(len);
        double[] magnitude = new double[len];

        for(int i=0; i < input.length; i++) {

            data[i*2] = input[i]/32768.0;
            data[(i*2) + 1] = 0.0;
            //Log.v(TAG, ""+ input[i]);
        }
        jFFT.complexForward(data);
        for(int i=0; i<len; i++) {

            double real = data[i*2];
            double img = data[(i*2) +1];
            magnitude[i] = Math.sqrt(real*real + img*img);
        }

        double peak = 0;
        int index=0;
        for(int i=0; i< len; i++) {
            if(peak < magnitude[i]) {
                peak = magnitude[i];
                index = i;
            }
        }
        baselines.add(peak);


    }


    private void computeVolume(float[] input) {
        double[] vol = new double[input.length];

        for(int i=0; i<input.length; i++) {
            vol[i] = input[i];
        }

        double sum = 0;

        for(int i=0; i<vol.length; i++) {
            double sample = vol[i];
            sum += sample*sample;
        }
        double rms = Math.sqrt(sum/vol.length);

        //calculating decibels
        double db = 0;
        if(rms != 0 )
            db = 20 * Math.log10(rms);


        magnitudes.add(rms);
    }

    private void writeAudioDataToFile() {
        //Write the output audio in byte
        String filePath = "/sdcard/8k16bitMono.pcm";
        File file = new File(filePath);
        if(file.exists()) {
            file.delete();
        }
        byte saudioBuffer[] = new byte[bufferSize];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        dArrayList.clear();
        magnitudes.clear();
        pitch.clear();

        while (isRecording) {
            // gets the voice output from microphone to byte format
            Log.v(TAG, "recording ...");
            int result = recorder.read(saudioBuffer, 0, bufferSize);
            float[] fBuffer = new float[saudioBuffer.length/2];

            double amp = 0;
            for(int i=0; i<fBuffer.length; i+=2) {
                float d = ((saudioBuffer[i+0] & 0xFF) | (saudioBuffer[i+1] << 8));
                fBuffer[i/2]= d;
                amp += d*d;
            }
            //computeSlidingFFT(fBuffer);
            computeVolume(fBuffer);
            computeSlidingFFT(fBuffer);
            double mean = Math.sqrt(amp/fBuffer.length);
            //Log.v(TAG, "getData : buffer length = " + bufferSize);
            //Log.v(TAG, "getData : mean = " + mean);
            dArrayList.add(mean);


            try {
                //  writes the data to file from buffer stores the voice buffer
                os.write(saudioBuffer, 0, bufferSize);

            } catch (IOException e) {
                e.printStackTrace();
            }
            /*ByteBuffer buffer = ByteBuffer.wrap(saudioBuffer);
            int status = encoder.write(buffer, saudioBuffer.length);
            if(status == result) {
                Log.v(TAG, "able to write flac file");

            } else {
                Log.v(TAG, "Unable to write flac file");
            }*/
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void PlayShortAudioFileViaAudioTrack(String filePath) throws IOException{
        // We keep temporarily filePath globally as we have only two sample sounds now..
        if (filePath==null)
            return;

        //Reading the file..
        File file = new File(filePath); // for ex. path= "/sdcard/samplesound.pcm" or "/sdcard/samplesound.wav"
        byte[] byteData = new byte[(int) file.length()];
        Log.d(TAG, (int) file.length()+"");

        FileInputStream in = null;
        try {
            in = new FileInputStream( file );
            in.read( byteData );
            in.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Set and push to audio track..
        int intSize = android.media.AudioTrack.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS_OUT, RECORDER_AUDIO_ENCODING);
        Log.d(TAG, intSize+"");

        AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS_OUT, RECORDER_AUDIO_ENCODING, intSize, AudioTrack.MODE_STREAM);
        if (at!=null) {
            at.play();
            // Write the byte array to the track
            at.write(byteData, 0, byteData.length);
            at.stop();
            at.release();
        }
        else
            Log.d(TAG, "audio track is not initialised ");


    }
    public static double[] load16BitPCMRawDataFileAsDoubleArray(File file) {
        InputStream in = null;
        if (file.isFile()) {
            long size = file.length();
            try {
                in = new FileInputStream(file);
                return readStreamAsDoubleArray(in, size);
            } catch (Exception e) {
            }
        }
        return null;
    }
    public static double[] readStreamAsDoubleArray(InputStream in, long size)
            throws IOException {
        int bufferSize = (int) (size / 2);
        double[] result = new double[bufferSize];
        DataInputStream is = new DataInputStream(in);
        for (int i = 0; i < bufferSize; i++) {
            result[i] = is.readShort()/32768.0;
        }
        return result;
    }

    public short[] shortMe(byte[] bytes) {
        short[] out = new short[bytes.length/2];
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        for(int i=0; i < out.length; i++) {
            out[i] = bb.getShort();
        }
        return out;
    }
    public double[] doubleMe(short[] pcms) {
        double[] doublers = new double[pcms.length];
        for(int i=0; i < pcms.length; i++) {
            //doublers[i] = pcms[i]/32768.0;
            doublers[i] = pcms[i];
        }
        return doublers;
    }

    public static double[] readStreamAsDB(InputStream in, long size)
            throws IOException {
        int bufferSize = (int) (size / 2);
        double[] result = new double[bufferSize];
        DataInputStream is = new DataInputStream(in);
        for (int i = 0; i < bufferSize; i++) {
            double d = is.readShort();

            if(d == 0) {
                result[i] = 0;
            } else {
                double dd = 20.0 * Math.log10(d / 65535.0);
                result[i] = dd;
            }
            if(result[i]== Double.NaN ){
                Log.v(TAG, "d = " + d);

            }

        }


        return result;
    }



    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_button: {
                    enableButtons(true);
                    startRecording();

                    break;
                }
                /*case R.id.stop_button: {
                    enableButtons(false);
                    try {
                        stopRecording();
                    } catch (IOException e) {
                        //  TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }*/
            }
        }
    };



    private void stopRecording() throws IOException {
        //  stops the recording activity
        isRecording = false;
        if (null != recorder) {
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
            Log.v(TAG, "Start Play ");
            PlayShortAudioFileViaAudioTrack("/sdcard/8k16bitMono.pcm");
            Log.v(TAG, "End Play ");
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
    public void baseAction(View view) {

        recorder = new AudioRecord(AUDIO_SOURCE, RECORDER_SAMPLERATE, RECORDER_CHANNELS_IN, RECORDER_AUDIO_ENCODING, bufferSize);
        // Starts recording from the AudioRecord instance.
        if(recorder == null) {
            Log.v(TAG, "recorder is null");

        }
        recorder.startRecording();
        baselineTimer.start();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {
            public void run() {
                computeBaseline();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();

    }
    public void playAction(View view) {

        try {
            PlayShortAudioFileViaAudioTrack("/sdcard/8k16bitMono.pcm");
        } catch(Exception e) {
            Log.v(TAG, "could not play audio file");
        }


    }
    private void computeBaseline()
    {
        byte buffer[] = new byte[bufferSize];
        while (isRecording) {
            // gets the voice output from microphone to byte format
            //Log.v(TAG, "recording baseline ...");
            int result = recorder.read(buffer, 0, bufferSize);
            float[] fBuffer = new float[buffer.length/2];

            for(int i=0; i<fBuffer.length; i+=2) {
                float d = ((buffer[i+0] & 0xFF) | (buffer[i+1] << 8));
                fBuffer[i/2]= d;
            }
            //computeBaselineFFT(fBuffer);
            computeNoise(fBuffer);

        }





    }

    private void computeNoise(float[] input){
        double[] vol = new double[input.length];

        for(int i=0; i<input.length; i++) {
            vol[i] = input[i];
        }

        double sum = 0;

        for(int i=0; i<vol.length; i++) {
            double sample = vol[i];
            sum += sample*sample;
        }
        double rms = Math.sqrt(sum/vol.length);

        baselines.add(rms);

    }

    public void freqAction(View v) {
        Intent intent = new Intent(this, PitchActivity.class);
        double[] frequencies = new double[pitch.size()-1];
        Log.v(TAG, "number of pitches =" + pitch.size());
        double maxFreq = 0;
        double avgFreq = 0;
        for(int i=0; i<frequencies.length; i++) {
            frequencies[i] = pitch.get(i);
            if(maxFreq < frequencies[i]){
                maxFreq = frequencies[i];

            }
            avgFreq += frequencies[i];
        }
        avgFreq = avgFreq/frequencies.length;
        double std = 0;
        for(int i=0; i<frequencies.length; i++) {
            double diff = frequencies[i] - avgFreq;
            std += (diff * diff);
        }
        std = Math.sqrt(std/frequencies.length);
        intent.putExtra("data", frequencies);
        intent.putExtra("max", maxFreq);
        intent.putExtra("average", avgFreq);
        intent.putExtra("std", std);
      intent.putExtra("userId", name);
       intent.putExtra("testDate", testDate);
        intent.putExtra("testMode", testMode);

        this.startActivity(intent);


    }


    public void energyAction (View view) {
        Intent intent = new Intent(this, GraphActivity.class);
        double[] dArray = new double[magnitudes.size()-1];


        for(int i=1; i < magnitudes.size(); i++) {
            dArray[i-1] = magnitudes.get(i);

        }

        int fadeIndex=0;
        for(int i=dArray.length-1; i>=0; i--) {
            if(dArray[i] > noiseBaseline) {
                break;
            }
            fadeIndex++;
        }

        fadeIndex = dArray.length-fadeIndex;
        int startIndex = 0;
        for(int i=0; i<dArray.length; i++) {
            if(dArray[i] >= noiseBaseline) {
                break;
            }
            startIndex++;
        }

        //calculate gaps in speech
        int audioLen = fadeIndex - startIndex;

        int gaps =0;
        for(int i=startIndex; i<fadeIndex; i++) {
            if(dArray[i] <= noiseBaseline) {
                gaps++;
            }

        }

        //standard deviation
        double stdDev = 0;
        double mean = 0;
        double maxVol = 0;
        for(int i=startIndex; i<fadeIndex; i++){
            mean += dArray[i];
            if(maxVol < dArray[i]){
                maxVol = dArray[i];
            }
        }
        mean = mean/audioLen;

        for(int i=startIndex; i < fadeIndex; i++){
            double diff = dArray[i] - mean;
            stdDev += diff * diff;
        }
        stdDev = Math.sqrt(stdDev/audioLen);


        intent.putExtra("data", dArray);
        intent.putExtra("avgValue", mean);
        intent.putExtra("maxValue", maxVol);
        intent.putExtra("fadeIndex", fadeIndex);
        intent.putExtra("startIndex", startIndex);
        intent.putExtra("audioLen", audioLen);
        intent.putExtra("gaps", gaps);
        intent.putExtra("stdDev", stdDev);
        intent.putExtra("userId", name);
        intent.putExtra("testDate", testDate);
        intent.putExtra("testMode", testMode);



        this.startActivity(intent);

    }




    public void pitchAction(View v) {
        Intent intent = new Intent(this, PitchActivity.class);
        double[] dArray = new double[dArrayList.size()];
        double max=0, average=0;
        for(int i=0; i<pitch.size(); i++) {

            double val = pitch.get(i);
            if(max < val ) {
                max = val;
            }
            average += val;
            dArray[i] = val;
        }
        average = average/dArrayList.size();
        intent.putExtra("data", dArray);
        intent.putExtra("average", average);
        intent.putExtra("max", max);

        this.startActivity(intent);
    }

    //old code
    private double[] getFFTData() {

        String filePath = "/sdcard/8k16bitMono.pcm";

        File file = new File(filePath);
        byte[] bytes = new byte[(int) file.length()];
        Log.d(TAG, (int) file.length()+"");

        FileInputStream in = null;
        try {
            in = new FileInputStream( file );
            in.read( bytes );
            in.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //double[] voiceData =  load16BitPCMRawDataFileAsDoubleArray(file);
        double[] voiceData = doubleMe(shortMe(bytes));
        //Log.v(TAG, "number of samples = " + voiceData.length);


        int len = voiceData.length;
        int n = 2;
        while (n < len) {
            n = n * 2;
        }
        if (n > len) {
            n = n / 2;
        }
        double[] real = new double[n];
        double[] img = new double[n];
        for (int i = 0; i < n; i++) {
            real[i] = voiceData[i];
            img[i] = 0;
        }



        ArrayList<Double> amps = new ArrayList<Double>();

        //Log.v(TAG, "length of magnitudes = " + real.length);
        for (int i = 1; i < real.length/2; i++) {
            amps.add(Math.sqrt(real[i] * real[i] + img[i] * img[i]));
            //Log.v(TAG, "mag = " + magnitudes[i]);
        }

        double[] magnitudes = new double[amps.size()];
        for(int i=0; i < amps.size(); i++) {
            magnitudes[i] = amps.get(i);
        }
        maxAmp = magnitudes[0];
        int pos = 0;
        for(int i=1; i<magnitudes.length; i++) {
            if(maxAmp < magnitudes[i]) {
                maxAmp = magnitudes[i];
                pos = i;
            }
        }

        maxMagnitude = (float)(maxAmp);

        //find how many frequencies that are close to the max

        double ff = (maxAmp + 1) * ((double) 8000/n);
        //Log.v(TAG, "max amplitude =" + maxAmp);
        //Log.v(TAG, "pos = " + ff);

        //find how many frequencies that are close to the max
        freqCount = 0;
        double average = 0;
        float avg = 0;
        float sum = 0;
        for (int i = 0; i < magnitudes.length; i++) {
            sum += magnitudes[i];
        }

        avg = sum/magnitudes.length;
        avgMagnitude = avg;



        frequencies = new ArrayList<Double>();
        for(int i=0; i<magnitudes.length; i++) {
            double match = 0.9 * maxAmp;
            average += magnitudes[i];
            if(magnitudes[i] >= match) {
                double freq = (i+1) * ((double)8000/n);
                frequencies.add(freq);
                freqCount++;
                Log.v(TAG, "pos = " + freq);
            }

        }
        average = average/magnitudes.length;
        double variation = 0;
        for(int i=0; i< magnitudes.length; i++) {
            double diff = magnitudes[i] - average;
            variation += diff * diff;
        }
        stdDev = Math.sqrt(variation/magnitudes.length);
        //Log.v(TAG, "count = " + count);
        //Log.v(TAG, "std dev = " + stdDev);

        return magnitudes;
    }

    //old code
    private double[] getJFFTData(){

        String filePath = "/sdcard/8k16bitMono.pcm";

        File file = new File(filePath);
        int numBytes = (int)file.length();
        byte[] bytes = new byte[(int) file.length()];

        FileInputStream in = null;
        try {
            in = new FileInputStream( file );
            in.read( bytes );
            in.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        double[] voiceData = doubleMe(shortMe(bytes));
        int fftLen = voiceData.length;
        int n = 2;
        while (n < fftLen) {
            n = n * 2;
        }
        if (n > fftLen) {
            n = n / 2;
        }
        double[] fftData = new double[n];
        for(int i=0 ; i < n; i++ ){
            fftData[i] = voiceData[i];
        }

        jFFT = new DoubleFFT_1D(n);
        jFFT.realForward(fftData);

        double[] re = new double[n/2];
        double[] im  = new double[n/2];
        double[] magnitude = new double[n/2];
        magnitude[0] = fftData[0];

        for(int i=1; i < n/2; i++) {
            re[i] = fftData[i*2];
            im[i] = fftData[(i*2) + 1];
            magnitude[i] = Math.sqrt((re[i] * re[i]) + (im[i] * im[i]));
            //Log.v(TAG, "magnitude = " + magnitude[i]);

        }

        double peak = -1.0;

        int fcount = 0;
        for(int i=0; i < n/2; i++) {
            if (peak < magnitude[i]) {
                peak = magnitude[i];
                fcount = i;
            }
        }

        maxAmp = peak;
        fFreq = fcount * 8000/n;
        freqCount = 0;
        frequencies = new ArrayList<Double>();
        stdDev = 0;

        return magnitude;

    }

    //old code
    public void filterAction(View v) {

        String filePath = "/sdcard/8k16bitMono.pcm";

        File file = new File(filePath);
        int numBytes = (int)file.length();
        byte[] bytes = new byte[(int) file.length()];

        FileInputStream in = null;
        try {
            in = new FileInputStream( file );
            in.read( bytes );
            in.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        double[] fftData = doubleMe(shortMe(bytes));
        int fftLen = fftData.length;


        jFFT = new DoubleFFT_1D(fftLen);
        double[] fftcopy = new double[fftData.length * 2];
        for(int i=0; i<fftData.length; i++) {
            fftcopy[i] = fftData[i];
            fftcopy[fftData.length+i] = 0;
        }
        for(int i=0; i < 1000; i++) {
            Log.v(TAG, " before fft val = " + fftcopy[i]);
        }
        jFFT.realForwardFull(fftcopy);
        jFFT.realInverse(fftcopy, false);

        double[] audioback = new double[fftData.length];

        for(int i=0; i < 1000; i++) {
            Log.v(TAG, " after ifft val = " + fftcopy[i]);
        }

        for(int i=0; i < fftData.length; i++){
            audioback[i] = fftcopy[i];

        }

        for(int i=0; i< 10; i++) {
            if(fftData[i] == audioback[i] ){
                Log.v(TAG, "data is same");
            } else {
                Log.v(TAG, fftData[i] + " != " + audioback[i]);
            }
        }



        //convert back to bytes
        short[] shortBack = new short[fftData.length];
        for(int i=0; i<audioback.length; i++){
            shortBack[i] = (short)audioback[i];
        }
        int len = fftData.length;
        ByteBuffer bb = ByteBuffer.allocate(len * 2);
        for(int i=0; i != len; ++i) {
            bb.putShort(shortBack[i]);
        }

        byte[] newBytes = bb.array();


        int intSize = android.media.AudioTrack.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS_OUT, RECORDER_AUDIO_ENCODING);
        Log.d(TAG, intSize+"");

        AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS_OUT, RECORDER_AUDIO_ENCODING, intSize, AudioTrack.MODE_STREAM);
        if (at!=null) {
            at.play();
            // Write the byte array to the track
            at.write(newBytes, 0, newBytes.length);
            at.stop();
            at.release();
        }
        else
            Log.d(TAG, "audio track is not initialised ");


    }




}
