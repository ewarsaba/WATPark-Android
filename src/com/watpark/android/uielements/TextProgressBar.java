package com.watpark.android.uielements;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.watpark.android.R;
import com.watpark.android.models.Lot;

public class TextProgressBar extends ProgressBar {  
    private String text;  
    private Paint textPaint;  
    private boolean drawableSet = false;
    private Context context;
    
    public TextProgressBar(Context context) {  
        super(context); 
        initialize(context);  
    }  
  
    public TextProgressBar(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        initialize(context);  
    }  
  
    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        initialize(context);
    }  
    
    private void initialize(Context context)
    {
    	this.context = context;
        text = "N/A";  
        textPaint = new Paint();  
        textPaint.setColor(Color.WHITE); 
    }
    
    public void setLot(Lot lot)
    {
    	// for whatever reason, if you set a progressDrawable that has already been set,
    	// the progress bar will disappear. Check prevents drawable from being set twice
    	if (!drawableSet)
    	{
    		drawableSet = true;
	    	double percentage = 100.0*lot.getLatestCount() / lot.getCapacity();
	    	
	    	if (percentage <= 60)
	    	{
	            this.setProgressDrawable(this.getContext().getResources().getDrawable(R.drawable.greenprogress));
	    	}
	    	else if (percentage <= 90)
	    	{
	            this.setProgressDrawable(this.getContext().getResources().getDrawable(R.drawable.yellowprogress));
	    	}
	    	else
	    	{
	            this.setProgressDrawable(this.getContext().getResources().getDrawable(R.drawable.redprogress));
	    	}
    	}
    	
    	this.setMax(lot.getCapacity());
		this.setProgress(lot.getLatestCount());
		this.setText(lot.getLatestCount() + "/" + lot.getCapacity());
    }
  
    @Override  
    protected synchronized void onDraw(Canvas canvas) {  
    	
        // First draw the regular progress bar, then custom draw our text  
        super.onDraw(canvas);  
        Rect bounds = new Rect();  
        textPaint.setTextSize(25);
        textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf"));
        textPaint.getTextBounds(text, 0, text.length(), bounds);  
        int x = getWidth() / 2 - bounds.centerX();  
        int y = getHeight() / 2 - bounds.centerY();
        canvas.drawText(text, x, y, textPaint);  
    }  
  
    public synchronized void setText(String text) {  
        this.text = text;  
        drawableStateChanged();  
    }  
  
    public void setTextColor(int color) {  
        textPaint.setColor(color);  
        drawableStateChanged();  
    }  
}  
