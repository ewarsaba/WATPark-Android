package com.watpark.android;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.watpark.android.activities.SplashActivity;
import com.watpark.android.models.Lot;

public class Util
{
	public static final long WEEK_MS = 604800000;
	public static final long DAY_MS = 86400000;
	public static final long HOUR_MS = 3600000;
	public static final int MINUTE_MS =  60000;
	public static final double UW_LAT = 43.47192;
	public static final double UW_LON = -80.544248;
	public static final int PREDICTION_RATIO = 3;
	public static GeoPoint getLocation(double latitude, double longitude)
	{
		return new GeoPoint((int) (latitude * 1e6), (int) (longitude * 1e6));
	}

	public static Drawable getImage(String lotName, Activity context)
	{
		if (lotName.toLowerCase().equals("c"))
		{
			return context.getResources().getDrawable(R.drawable.c);
		}
		else if (lotName.toLowerCase().equals("n"))
		{
			return context.getResources().getDrawable(R.drawable.n);
		}
		else if (lotName.toLowerCase().equals("w"))
		{
			return context.getResources().getDrawable(R.drawable.w);
		}
		else if (lotName.toLowerCase().equals("x"))
		{
			return context.getResources().getDrawable(R.drawable.x);
		}
		else
		{
			return context.getResources().getDrawable(R.drawable.c);
		}
	}
	
	public static Drawable getMapImage(String lotName, Activity context)
	{
		if (lotName.toLowerCase().equals("c"))
		{
			return context.getResources().getDrawable(R.drawable.cmap);
		}
		else if (lotName.toLowerCase().equals("n"))
		{
			return context.getResources().getDrawable(R.drawable.nmap);
		}
		else if (lotName.toLowerCase().equals("w"))
		{
			return context.getResources().getDrawable(R.drawable.wmap);
		}
		else if (lotName.toLowerCase().equals("x"))
		{
			return context.getResources().getDrawable(R.drawable.xmap);
		}
		else
		{
			return context.getResources().getDrawable(R.drawable.cmap);
		}
	}
	
	
	public static void restartApp(Activity activity)
	{
		Intent intent = new Intent(
				activity.getApplicationContext(),
				SplashActivity.class);
		intent.putExtra("restart", true);
		activity.startActivity(intent);
	}
	
	public static Date getPastDate(int countType)
	{
		Date now = new Date();
		long diff = Util.HOUR_MS;
		
		if (countType == Lot.DAY_COUNTS)
		{
			diff = Util.DAY_MS;
		}
		else if (countType == Lot.WEEK_COUNTS)
		{
			diff = Util.WEEK_MS;
		}
		
		return new Date(now.getTime() - diff);
	}
}
