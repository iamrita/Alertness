package com.amrita2015.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by mita on 2/9/15.
 */
public class DrawShaking extends View {
    private static String TAG = "DrawPath";
    private ArrayList<Float> path = new ArrayList<Float>();
    private boolean drawPath = false;
    public DrawShaking(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }


    public DrawShaking(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }


    public DrawShaking(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    protected void onDraw(Canvas canvas){
        Paint gray = new Paint();
        gray.setAntiAlias(true);
        gray.setStrokeWidth(10);
        gray.setColor(Color.LTGRAY);
        gray.setStyle(Paint.Style.STROKE);
        gray.setTextSize(25);
        Paint blue = new Paint();
        blue.setAntiAlias(true);
        blue.setStrokeWidth(5);
        blue.setColor(Color.BLUE);
        blue.setStyle(Paint.Style.STROKE);
        blue.setTextSize(25);

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int midY = h/2;

        Log.v(TAG, "height of screen = " + h);
        int[] xCoord = new int[path.size()+1];
        int[] yCoord = new int[path.size()+1];
        xCoord[0]=0;
        yCoord[0]=h/2;
        int xInterval = (int)((float)w/(float)path.size());
        canvas.drawLine(0,midY,w,midY, gray);
        if(drawPath) {

            for(int i=0;i < path.size(); i++) {
                xCoord[i+1] = xCoord[i] + xInterval;
                yCoord[i+1] = (int)(midY + (path.get(i)*2));
            }
            for(int i=0; i<xCoord.length-1; i++) {
                canvas.drawLine(xCoord[i],yCoord[i], xCoord[i+1],yCoord[i+1], blue);
            }
        }



    }
    public void setPath(ArrayList<Float> path){

        this.path = path;
        drawPath = true;
        invalidate();

    }
}