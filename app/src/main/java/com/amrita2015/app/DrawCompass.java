package com.amrita2015.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.text.NumberFormat;


public class DrawCompass extends View {


    private float position = 0;

    public DrawCompass(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }


    public DrawCompass(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }


    public DrawCompass(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }



    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        //myRect.set(0, 0, canvas.getWidth(), canvas.getHeight()/2);
        Paint blue = new Paint();
        blue.setAntiAlias(true);
        blue.setStrokeWidth(5);
        blue.setColor(Color.BLUE);
        blue.setStyle(Paint.Style.STROKE);
        blue.setTextSize(25);

        Paint black = new Paint();
        black.setAntiAlias(true);
        black.setStrokeWidth(3);
        black.setColor(Color.BLACK);
        black.setStyle(Paint.Style.STROKE);
        black.setTextSize(35);


        int xPoint = getMeasuredWidth() / 2;
        int yPoint = getMeasuredHeight() / 2;
        float radius = (float) (Math.max(xPoint, yPoint) * 0.6);
        canvas.drawCircle(xPoint, yPoint, radius, blue);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), blue);
        canvas.drawLine(xPoint,
                yPoint,
                (float) (xPoint + radius
                        * Math.sin((double) (-position) / 180 * 3.143)),
                (float) (yPoint - radius
                        * Math.cos((double) (-position) / 180 * 3.143)), blue);

        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        canvas.drawText(formatter.format(position), xPoint, yPoint, black);




    }

    public void updateData(float pos) {
        this.position = pos;
        invalidate();
    }


}
