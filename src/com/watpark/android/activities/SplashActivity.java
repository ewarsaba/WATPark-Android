package com.watpark.android.activities;

import android.content.Intent;
import android.os.Bundle;

import com.watpark.android.R;
import com.watpark.android.Session;

public class SplashActivity extends BaseActivity
{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		boolean restart = (this.getIntent().getExtras() != null) ? this.getIntent().getExtras().getBoolean("restart") : false;
		if (restart)
		{
			Session.finishActivities();
		}
	}

	@Override
	public void updateLotInfo()
	{
		// Get the item that was clicked
		Intent intent = new Intent(this.getApplicationContext(),
				LotListActivity.class);
		startActivity(intent);
	}

}
