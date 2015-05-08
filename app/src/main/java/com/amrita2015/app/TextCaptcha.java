package com.amrita2015.app;
import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Bitmap.Config;
import android.graphics.Typeface;
import android.util.Log;

public class TextCaptcha extends Captcha {

	protected TextOptions options;
	private int wordLength;
    private char[] letters;

	public enum TextOptions{
		UPPERCASE_ONLY,
		LOWERCASE_ONLY,
		NUMBERS_ONLY,
		LETTERS_ONLY,
		NUMBERS_AND_LETTERS
	}

	public TextCaptcha(int wordLength, TextOptions opt){
		this(0, 0, wordLength, opt);
	}

	public TextCaptcha(int width, int height, int wordLength, TextOptions opt){
    	setHeight(height);
    	setWidth(width);
    	this.options = opt;
    	usedColors = new ArrayList<Integer>();
    	this.wordLength = wordLength;
    	this.image = image();
	}

	@Override
	protected Bitmap image() {
        y = 170;
        x = 35;
	    LinearGradient gradient = new LinearGradient(0, 0, getWidth() / this.wordLength, getHeight() / 2, Color.BLACK, Color.BLACK, Shader.TileMode.MIRROR);
	    Paint p = new Paint();
	    p.setDither(true);
	    p.setShader(gradient);
	    Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
	    Canvas c = new Canvas(bitmap);
	   // c.drawRect(0, 0, getWidth(), getHeight(), p);
	    Paint tp = new Paint();
	    tp.setDither(true);
	    Typeface tf = Typeface.create("sans-serif-condensed",Typeface.ITALIC);
	    tp.setTextSize(125);
	    tp.setTypeface(tf);

	    Random r = new Random(System.currentTimeMillis());
	    CharArrayWriter cab = new CharArrayWriter();
	    this.answer = "";
		for(int i = 0; i < this.wordLength; i ++){
			int u_l_n = r.nextInt(3);
			char ch = ' ';
		    switch(u_l_n){
		    //UpperCase
		    case 0:
		    	ch = (char)(r.nextInt(91 - 65) + (65));
		    	break;
		    //LowerCase
		    case 1:
		    	ch = (char)(r.nextInt(123 - 97) + (97));
		    	break;
		    //Numbers
		    case 2:
		    	ch = (char)(r.nextInt(58 - 49) + (49));
		    	break;			    	
		    }
			cab.append(ch);
			this.answer += ch;
		}

	    char[] data = cab.toCharArray();
        letters = data;
	    for (int i=0; i<data.length; i++) {
            Log.v("Starting x coord is " , this.x +"");
            Log.v("Starting y coord is ", this.y + "");
	    	this.x += 100;
	        Canvas cc = new Canvas(bitmap);
        	tp.setTextSkewX(r.nextFloat() - r.nextFloat());
	        tp.setColor(color());
	        cc.drawText(data, i, 1, this.x, this.y, tp);
	        tp.setTextSkewX(0);
	    }
	    return bitmap;
	}

    public char[] getLetters() {
        return letters;
    }

}