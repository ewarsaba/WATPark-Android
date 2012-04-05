package com.watpark.android.activities;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidplot.Plot;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.watpark.android.LotChartListener;
import com.watpark.android.LotData;
import com.watpark.android.R;
import com.watpark.android.Session;
import com.watpark.android.Util;
import com.watpark.android.models.Lot;
import com.watpark.android.models.LotCount;
import com.watpark.android.uielements.TextProgressBar;

/* Activity for showing a single lot's info */
public class LotActivity extends BaseActivity implements LotChartListener
{
	private int lotID = -1;
	int currentChart = -1;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		try
		{
			super.onCreate(savedInstanceState);
			lotID = this.getIntent().getExtras().getInt("lot");
			this.setContentView(R.layout.lot);
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
		Lot lot = LotData.getLots().get(lotID);

		// set lot name
		TextView title = (TextView) findViewById(R.id.label);

		title.setText("Lot " + lot.getName());

		// set lot icon
		ImageView icon = (ImageView) findViewById(R.id.icon);
		icon.setImageDrawable(Util.getImage(lot.getName(), this));

		// add center map preview to lot
		ImageView mapView = (ImageView) findViewById(R.id.mapView);
		mapView.setImageDrawable(Util.getMapImage(lot.getName(), this));

		// set hours of operation
		SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
		TextView hoursView = (TextView) findViewById(R.id.hours);
		hoursView.setText(" " + timeFormat.format(lot.getOpenTime()) + " to "
				+ timeFormat.format(lot.getCloseTime()));

		// set map link listener
		View mapLink = findViewById(R.id.mapLink);
		OnClickListener mapListener = new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// launch map activity
				Lot lot = LotData.getLots().get(lotID);
				Intent intent = new Intent(
						LotActivity.this.getApplicationContext(),
						LotMapActivity.class);
				intent.putExtra("latitude", (int) (lot.getLatitude() * 1e6));
				intent.putExtra("longitude", (int) (lot.getLongitude() * 1e6));
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		};
		mapLink.setOnClickListener(mapListener);
		mapView.setOnClickListener(mapListener);

		// set direction link listener
		View directionsLink = findViewById(R.id.dirLink);
		directionsLink.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// launch Google Maps with directions to lot
				Lot lot = LotData.getLots().get(lotID);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://maps.google.com/maps?f=d&daddr="
								+ lot.getLatitude() + "," + lot.getLongitude()
								+ " (Lot " + lot.getName() + ")"));
				intent.setComponent(new ComponentName(
						"com.google.android.apps.maps",
						"com.google.android.maps.MapsActivity"));
				startActivity(intent);
			}
		});

		initializeCharts();
		updateLotInfo();
	}

	private void setUpChart(Lot lot, XYPlot chart, XYSeries countSeries, XYSeries predictionSeries, int type,
			int minRange, int maxRange)
	{
		// create chart arrays
		chart.getGraphWidget().getGridBackgroundPaint().setColor(Color.BLACK); // chart
																				// background
		chart.getGraphWidget().getGridLinePaint().setColor(Color.LTGRAY); // grid
		chart.getGraphWidget().getDomainOriginLinePaint()
				.setColor(Color.TRANSPARENT); // axis colors
		chart.getGraphWidget().getRangeOriginLinePaint()
				.setColor(Color.TRANSPARENT);

		chart.setBorderStyle(Plot.BorderStyle.NONE, null, null); // chart

		// setup our line fill paint to be a slightly transparent gradient:
		Paint lineFill = new Paint();
		lineFill.setAlpha(0);

		LineAndPointFormatter countForm = new LineAndPointFormatter(
				Color.WHITE, Color.WHITE, Color.WHITE);
		countForm.getVertexPaint().setStrokeWidth(0);
		countForm.getLinePaint().setStrokeWidth(3);
		countForm.setFillPaint(lineFill);
		chart.addSeries(countSeries, countForm);
		
		LineAndPointFormatter predForm = new LineAndPointFormatter(
				Color.parseColor("#FCD300"), Color.parseColor("#FCD300"), Color.parseColor("#FCD300"));
		predForm.getVertexPaint().setStrokeWidth(0);
		predForm.getLinePaint().setStrokeWidth(3);
		predForm.setFillPaint(lineFill);
		chart.addSeries(predictionSeries, predForm);
		
		// set domain tick spacing
		chart.setDomainStep(XYStepMode.SUBDIVIDE, 5);
		chart.setRangeStep(XYStepMode.SUBDIVIDE, 5);

		// set axist text format
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Regular.ttf");
		chart.getGraphWidget().getDomainLabelPaint().setTextSize(20);
		chart.getGraphWidget().getDomainLabelPaint().setTypeface(tf);
		chart.getGraphWidget().getDomainLabelPaint().setColor(Color.WHITE);
		chart.getGraphWidget().getRangeLabelPaint().setTextSize(20);
		chart.getGraphWidget().getRangeLabelPaint().setTypeface(tf);
		chart.getGraphWidget().getRangeLabelPaint().setColor(Color.WHITE);
		chart.getGraphWidget().getDomainOriginLabelPaint().setTextSize(20);
		chart.getGraphWidget().getRangeOriginLabelPaint().setTextSize(20);
		chart.getGraphWidget().getDomainOriginLabelPaint().setTypeface(tf);
		chart.getGraphWidget().getRangeOriginLabelPaint().setTypeface(tf);
		
		// set up legend
		chart.getLegendWidget().getTextPaint().setTypeface(tf);
		
		// remove background and useless widgets
		chart.setBackgroundPaint(null);
		chart.getGraphWidget().setBackgroundPaint(null);
		chart.getGraphWidget().setMarginRight(25);
		chart.getGraphWidget().setMarginLeft(25);
		chart.getGraphWidget().setMarginBottom(20);
		chart.getGraphWidget().setMarginTop(20);
		chart.getLayoutManager().remove(chart.getDomainLabelWidget());
		chart.getLayoutManager().remove(chart.getTitleWidget());

		chart.setRangeLabel("number of cars");
		chart.getRangeLabelWidget().getLabelPaint().setTextSize(20);
		chart.getRangeLabelWidget().setHeight(150);
		chart.getRangeLabelWidget().setWidth(50);

		// get rid of decimal points in our range labels:
		chart.setRangeValueFormat(new DecimalFormat("0"));

		chart.setDomainValueFormat(new MyDateFormat(type));

		chart.setRangeBoundaries(minRange, maxRange, BoundaryMode.AUTO);
		
		// by default, AndroidPlot displays developer guides to aid in
		// laying out your plot.
		// To get rid of them call disableAllMarkup():
		chart.disableAllMarkup();
	}

	private class MyDateFormat extends Format
	{

		private static final long serialVersionUID = 4147534009478364348L;
		private SimpleDateFormat dateFormat = null;

		public MyDateFormat(int type)
		{
			if (type == Lot.HOUR_COUNTS || type == Lot.DAY_COUNTS)
			{
				dateFormat = new SimpleDateFormat("h:mm");
			}
			else
			{
				dateFormat = new SimpleDateFormat("MMM d");
			}
		}

		@Override
		public StringBuffer format(Object obj, StringBuffer toAppendTo,
				FieldPosition pos)
		{
			long timestamp = ((Number) obj).longValue();
			Date date = new Date(timestamp);
			return dateFormat.format(date, toAppendTo, pos);
		}

		@Override
		public Object parseObject(String source, ParsePosition pos)
		{
			return null;

		}

	}

	private FrameLayout getChartFrame(int type)
	{
		if (type == Lot.HOUR_COUNTS)
		{
			return (FrameLayout) findViewById(R.id.hourChart);
		}
		else if (type == Lot.DAY_COUNTS)
		{
			return (FrameLayout) findViewById(R.id.dayChart);
		}
		else if (type == Lot.WEEK_COUNTS)
		{
			return (FrameLayout) findViewById(R.id.weekChart);
		}

		return null;
	}

	private TextView getTextView(int type)
	{
		if (type == Lot.HOUR_COUNTS)
		{
			return (TextView) findViewById(R.id.zoomhour);
		}
		else if (type == Lot.DAY_COUNTS)
		{
			return (TextView) findViewById(R.id.zoomday);
		}
		else if (type == Lot.WEEK_COUNTS)
		{
			return (TextView) findViewById(R.id.zoomweek);
		}
		return null;
	}

	@Override
	public void updateChart(int lotID, int countType)
	{
		if (!this.isActive())
		{
			return;
		}

		Lot lot = null;

		if (this.lotID != lotID)
		{
			return;
		}
		else
		{
			lot = LotData.getLots().get(lotID);
		}

		FrameLayout frame = getChartFrame(countType);

		// create row view
		frame.removeAllViews();
		LayoutInflater inflater = this.getLayoutInflater();
		View view = inflater.inflate(R.layout.lot_chart, null, true);
		frame.addView(view);

		XYPlot chart = (XYPlot) view.findViewById(R.id.countChart);

		ArrayList<LotCount> lotCounts = lot.getCounts(countType);
		if (lotCounts != null && lotCounts.size() > 0)
		{
			int lastCount = -1;
			int minCount = Integer.MAX_VALUE;
			int maxCount = Integer.MIN_VALUE;

			ArrayList<Integer> counts = new ArrayList<Integer>();
			ArrayList<Long> counts_time = new ArrayList<Long>();
			
			for (int i = 0; i < lotCounts.size(); i++)
			{
				LotCount lotCount = lotCounts.get(i);
				counts.add(lotCount.getCount());
				counts_time.add(lotCount.getTimePolled().getTime());

				maxCount = lotCount.getCount() > maxCount ? lotCount.getCount() : maxCount;
				minCount = lotCount.getCount() < minCount ? lotCount.getCount() : minCount;

				if (i == lotCounts.size() - 1)
				{
					lastCount = lotCount.getCount();
				}
			}

			ArrayList<Integer> predictions = new ArrayList<Integer>();
			ArrayList<Long> predictions_time = new ArrayList<Long>();
			ArrayList<LotCount> lotPredictions = lot.getPredictions(countType);
			
			if (lotPredictions != null && lotPredictions.size() > 0)
			{
				double diff = 1.0*lot.getLatestCount()/lotPredictions.get(0).getCount();
				for (int i = 0; i < lotPredictions.size(); i++)
				{
					LotCount lotCount = lotPredictions.get(i);
					// adjust the predictions for the hour chart to smooth out the line
					int count = (int)(lotCount.getCount()*(countType == Lot.HOUR_COUNTS ? diff : 1));
					predictions.add(count);
					predictions_time.add(lotCount.getTimePolled().getTime());
					maxCount = count > maxCount ? count : maxCount;
					minCount = count < minCount ? count : minCount;
				}
			}
			
			// add point for current time
			if (lastCount != -1)
			{
				long timeNow = new Date().getTime();
				long timePrevious = timeNow - Util.HOUR_MS;

				if (countType == Lot.DAY_COUNTS)
				{
					timePrevious = timeNow - Util.DAY_MS;
				}
				else if (countType == Lot.WEEK_COUNTS)
				{
					timePrevious = timeNow - Util.WEEK_MS;
				}

				if (lotPredictions != null)
				{
					predictions.add(0, lastCount);
					predictions_time.add(0, timeNow);
				}
				
				counts.add(lastCount);
				counts_time.add(timeNow);
				
				if (counts_time.get(0) > timePrevious)
				{
					counts.add(0, counts.get(0));
					counts_time.add(0, timePrevious);
				}
			}

			XYSeries countSeries = new SimpleXYSeries(counts_time, counts, "Past Counts");
			XYSeries predSeries = new SimpleXYSeries(predictions_time, predictions, "Predictions");
			
			setUpChart(lot, chart, countSeries, predSeries, countType, (minCount / 5) * 5,
					(maxCount / 5 + 1) * 5);
		}
		else
		{
			View nodata = inflater.inflate(R.layout.nodata_chart, null, true);
			frame.removeAllViews();
			frame.addView(nodata);
			frame.setVisibility(View.VISIBLE);
		}
	}

	private void initializeCharts()
	{
		String status = LotData.getLots().get(lotID).getStatus();
		
		if (status.equals(Lot.NODATA_STATUS)
				|| status.equals(Lot.MAINTENANCE_STATUS))
		{
			View messageLayout = this.getLayoutInflater().inflate(
					R.layout.status_message, null, true);
			
			TextView message = (TextView) messageLayout
					.findViewById(R.id.message);
			
			message.setText(this.getResources().getString(
					status.equals(Lot.NODATA_STATUS) ? R.string.nodataStatus
							: R.string.maintenanceStatus));
			
			FrameLayout frame = (FrameLayout) findViewById(R.id.statusMessage);
			frame.removeAllViews();
			frame.addView(messageLayout);
			frame.setVisibility(View.VISIBLE);
			return;
		}

		View tabs = findViewById(R.id.zoomtabs);
		tabs.setVisibility(View.VISIBLE);

		for (int i = 0; i <= 2; i++)
		{
			if (currentChart == -1 && i == Lot.HOUR_COUNTS)
			{
				currentChart = i;
			}

			if (currentChart == i)
			{
				FrameLayout frame = getChartFrame(i);
				frame.setVisibility(View.VISIBLE);
				retrieveChart(frame, new Date(new Date().getTime()
						- Util.HOUR_MS), i);
			}
		}

		Date now = new Date();
		long nowMS = now.getTime();

		TextView hourTab = (TextView) findViewById(R.id.zoomhour);
		hourTab.setOnClickListener(new TabListener(Lot.HOUR_COUNTS, new Date(
				nowMS - Util.HOUR_MS)));

		TextView dayTab = (TextView) findViewById(R.id.zoomday);
		dayTab.setOnClickListener(new TabListener(Lot.DAY_COUNTS, new Date(
				nowMS - Util.DAY_MS)));

		TextView weekTab = (TextView) findViewById(R.id.zoomweek);
		weekTab.setOnClickListener(new TabListener(Lot.WEEK_COUNTS, new Date(
				nowMS - Util.WEEK_MS)));

	}

	private class TabListener implements OnClickListener
	{
		int type;
		Date startTime;

		public TabListener(int type, Date startTime)
		{
			this.type = type;
			this.startTime = startTime;
		}

		@Override
		public void onClick(View v)
		{
			if (currentChart != type)
			{
				FrameLayout frame = getChartFrame(currentChart);
				TextView tab = getTextView(currentChart);

				frame.setVisibility(View.GONE);
				tab.setBackgroundDrawable(LotActivity.this.getResources()
						.getDrawable(R.drawable.rounded_box_black));

				frame = getChartFrame(type);
				tab = getTextView(type);

				frame.setVisibility(View.VISIBLE);
				tab.setBackgroundDrawable(LotActivity.this.getResources()
						.getDrawable(R.drawable.rounded_box_blue));

				retrieveChart(frame, startTime, type);
				currentChart = type;
			}
			else
			{
				FrameLayout frame = getChartFrame(currentChart);
				retrieveChart(frame, startTime, type);
			}

		}

	}

	private void retrieveChart(FrameLayout frame, Date startTime, int type)
	{

		Lot lot = LotData.getLots().get(lotID);
		ArrayList<LotCount> counts = lot.getCounts(type);

		if (counts != null)
		{
			if (counts.size() > 0)
			{
				long difference = new Date().getTime()
						- Session.getCountsLastUpdated(type);

				if (difference < 60000)
				{
					updateChart(lotID, type);
					return;
				}
			}
		}
		LayoutInflater inflater = LotActivity.this.getLayoutInflater();
		View view = inflater.inflate(R.layout.loading_chart, null, true);
		frame.removeAllViews();
		frame.addView(view);
		Session.retrieveLotCounts(startTime, type, lotID, LotActivity.this);
	}

	@Override
	public void updateLotInfo()
	{
		Lot lot = LotData.getLots().get(lotID);

		// set lot count progress
		TextProgressBar progressBar = (TextProgressBar) findViewById(R.id.progress);

		if (lot.getLatestCount() == -1)
		{
			progressBar.setVisibility(View.INVISIBLE);
		}
		else
		{
			progressBar.setLot(lot);
			progressBar.setVisibility(View.VISIBLE);
		}
	}
}
