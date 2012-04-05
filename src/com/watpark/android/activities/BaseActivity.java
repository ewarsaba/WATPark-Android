package com.watpark.android.activities;

import android.os.Bundle;

import com.google.android.maps.MapActivity;
import com.watpark.android.LotUpdateListener;
import com.watpark.android.Session;

public abstract class BaseActivity extends MapActivity implements LotUpdateListener {

	private boolean isActive = false;
	
	public boolean isActive()
	{
		return isActive;
	}

	@Override
	public abstract void updateLotInfo();
	
	
	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		isActive = true;
		Session.addActivity(this);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Session.removeActivity(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Session.removeListener(this);
		this.isActive = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Session.addListener(this);
		this.isActive = true;
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
