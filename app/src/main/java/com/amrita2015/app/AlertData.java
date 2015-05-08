package com.amrita2015.app;

public class AlertData {

    private String id;
    private long date;
    private String type;
    private float brainScore;
    private float balanceScore;
    private boolean balancePass;
    private float slurScore;
    private float maxAmplitude;
    private float avgAmplitude;
    private float stdAmplitude;
    private int audioLen;
    private int audioGaps;
    private float maxFFreq;
    private float avgFFreq;
    private float stdFFreq;
    private float accuracy;

    public AlertData(String id, long date, String type) {
        this.id = id;
        this.date = date;
        this.type = type;
    }

    public float getBalanceScore() {
        return balanceScore;
    }
    public float getBrainScore() {
        return brainScore;
    }
    public float getSlurScore() {
        return slurScore;
    }
    public float getAvgAmp() {
        return avgAmplitude;
    }
    public float getAvgFreq() {
        return avgFFreq;
    }

    public void setBrainScore(float score) {
        this.brainScore = score;
    }

    public void setSlurScore(float score) {
        this.slurScore = score;
    }

    public void setBalanceScore(float score) {
        this.balanceScore = score;
    }

    public void setBalancePass(boolean pass){
        this.balancePass = pass;
    }

    public void setMaxAmplitude(float maxAmp){
        this.maxAmplitude = maxAmp;
    }

    public void setAvgAmplitude(float maxAmp){
        this.avgAmplitude = maxAmp;
    }

    public void setStdAmplitude(float std) {
        this.stdAmplitude = std;
    }
    public void setAudioLen(int len) {
        audioLen = len;
    }
    public void setAudioGaps(int gaps) {
        audioGaps = gaps;
    }
    public void setMaxFFreq(float maxFFreq){
        this.maxFFreq = maxFFreq;
    }
    public void setAvgFFReq(float freq){
        this.avgFFreq = freq;
    }
    public void setStdFFreq(float std) {
        this.stdFFreq = std;
    }


    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getId() {
        return id;
    }
    public String getType() {
        return type;
    }

    public String toString(){

        String ret = "";
        ret = " max Amp : " + maxAmplitude + "\n avg Amp : " + avgAmplitude + "\n std Amp : " + stdAmplitude +
                "\n Len : " + audioLen + "\n Gaps : " +  audioGaps + "\n Max Freq : " + maxFFreq + "\n Avg Freq : "
                + avgFFreq + "\n std Freq : " + stdFFreq + "\n brainScore : " + brainScore + "\n slurScore : " + slurScore + "\n" +
                "balanceScore: " + balanceScore;

        return ret;
    }



}
