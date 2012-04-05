package com.watpark.android.uielements;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.watpark.android.LotData;
import com.watpark.android.R;
import com.watpark.android.Util;
import com.watpark.android.activities.LotActivity;
import com.watpark.android.models.Lot;

/* Custom overlay for displaying lot info on mapview */
public class MapOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Activity mContext;

	public MapOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		populate();
	}

	public MapOverlay(Drawable defaultMarker, Activity context) {
		super(boundCenterBottom(defaultMarker));
		this.mContext = context;
		populate();
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		
		// get lot from session map
		final Lot lot = LotData.getLots().get(Integer.valueOf(mOverlays.get(index).getSnippet()));

		// create dialog view
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.lotdialog, null);

		// set lot name
		TextView title = (TextView) layout.findViewById(R.id.label);
		title.setText("Lot " + lot.getName());

		// set lot icon
		ImageView icon = (ImageView) layout.findViewById(R.id.icon);
		icon.setImageDrawable(Util.getImage(lot.getName(), mContext));

		// set lot count progress
		TextProgressBar progressBar = (TextProgressBar) layout
				.findViewById(R.id.progress);
		if (lot.getLatestCount() == -1)
		{
			progressBar.setVisibility(View.INVISIBLE);
		}
		else
		{
			progressBar.setLot(lot);
		}
		
		View dirLink = layout.findViewById(R.id.dirLink);
		dirLink.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// launch Google Maps, show directions to lot
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://maps.google.com/maps?f=d&daddr="
								+ lot.getLatitude() + "," + lot.getLongitude()
								+ " (Lot " + lot.getName() + ")"));
				intent.setComponent(new ComponentName(
						"com.google.android.apps.maps",
						"com.google.android.maps.MapsActivity"));
				mContext.startActivity(intent);
			}
		});
		
		View statLink = layout.findViewById(R.id.statLink);
		statLink.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext,
						LotActivity.class);
				intent.putExtra("lot", lot.getID());
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				mContext.startActivity(intent);
				
			}
		});

		// show dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setView(layout);
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
		return true;
	}

}
