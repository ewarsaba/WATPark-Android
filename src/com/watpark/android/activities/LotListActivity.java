package com.watpark.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.watpark.android.LotData;
import com.watpark.android.R;
import com.watpark.android.Util;
import com.watpark.android.uielements.LotListAdapter;

/* Main page acitvity that lists lots and their current counts */
public class LotListActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lot_list);
		
		if (LotData.getLots().size() == 0)
		{
			Util.restartApp(this);
			return;
		}
		
		// set map link listener
		View mapLink = findViewById(R.id.mapLink);
		OnClickListener mapListener = new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// launch map activivity
				Intent intent = new Intent(
						LotListActivity.this.getApplicationContext(),
						LotMapActivity.class);
				intent.putExtra("latitude", (int) (Util.UW_LAT * 1e6));
				intent.putExtra("longitude", (int) (Util.UW_LON * 1e6));
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		};
		mapLink.setOnClickListener(mapListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (LotData.getLots().size() > 0) {
			updateLotInfo();
		}
	}

	@Override
	public void updateLotInfo() {
		Integer[] lotNames = new Integer[LotData.getLots().size()];
		LotData.getLots().keySet().toArray(lotNames);

		if (LotData.getLots().size() > 0) { // lots already added
			
			final ListView listView = (ListView) findViewById(R.id.lotList);
			
			listView.setAdapter(new LotListAdapter(
					LotListActivity.this, lotNames));
			
			listView.setOnItemClickListener(new OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id)
				{
					// Get the item that was clicked
					Intent intent = new Intent(LotListActivity.this.getApplicationContext(),
							LotActivity.class);
					intent.putExtra("lot", (Integer)listView.getAdapter().getItem(position));
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});
		}
	}
}