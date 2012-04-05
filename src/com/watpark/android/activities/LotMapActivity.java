package com.watpark.android.activities;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.watpark.android.LotData;
import com.watpark.android.R;
import com.watpark.android.Util;
import com.watpark.android.models.Lot;
import com.watpark.android.uielements.MapOverlay;

/* Activity for showing all lots on a map */
public class LotMapActivity extends BaseActivity
{

	private static final int DEFAULT_ZOOM = 16;
	private int centerLat = -1;
	private int centerLon = -1;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		try
		{
			super.onCreate(savedInstanceState);
			centerLat = this.getIntent().getExtras().getInt("latitude");
			centerLon = this.getIntent().getExtras().getInt("longitude");
			setContentView(R.layout.map);
		}
		catch (Exception e)
		{
			Util.restartApp(this);
		}
	}

	@Override
	public void onContentChanged()
	{
		super.onContentChanged();

		MapView mapView = (MapView) findViewById(R.id.fullmap);

		// center map
		GeoPoint center = new GeoPoint(centerLat, centerLon);

		mapView.getController().setCenter(center);
		mapView.getController().setZoom(DEFAULT_ZOOM);

		updateLotInfo();
	}

	@Override
	public void updateLotInfo()
	{
		MapView mapView = (MapView) findViewById(R.id.fullmap);

		// put lots on map
		List<Overlay> mapOverlays = mapView.getOverlays();
		mapOverlays.clear();

		for (int lotID : LotData.getLots().keySet())
		{

			Lot lot = LotData.getLots().get(lotID);

			// Get icon and set overlay
			Drawable drawable = Util.getImage(lot.getName(), this);
			;
			MapOverlay itemizedOverlay = new MapOverlay(drawable, this);

			// Get lot location
			GeoPoint point = Util.getLocation(lot.getLatitude(),
					lot.getLongitude());

			// Set lot information in the OverlayItem
			OverlayItem overlayItem = new OverlayItem(point, lot.getName(),
					String.valueOf(lot.getID()));
			OverlayItem.setState(drawable, OverlayItem.ITEM_STATE_FOCUSED_MASK);

			itemizedOverlay.addOverlay(overlayItem);
			mapOverlays.add(itemizedOverlay);
		}
	}
}
