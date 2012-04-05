package com.watpark.android;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.watpark.android.models.Lot;
import com.watpark.android.models.LotCount;

public class CountsTask extends AsyncTask<Void, Void, Void>
{

	private Date startTime = null;
	private int countType = -1;
	private int lotID = -1;
	private LotChartListener listener = null;
	private static Handler countHandler = new Handler();
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			API.DATE_FORMAT_COUNT);
	
	public void initialize(Date startTime, int countType, int lotID,
			LotChartListener listener)
	{
		this.startTime = startTime;
		this.countType = countType;
		this.lotID = lotID;
		this.listener = listener;
	}

	protected Void doInBackground(Void... params)
	{
		Date now = new Date();
		
		try
		{
			URI countsURI = new URI(API.COUNTS_URL + "?start_time="
					+ URLEncoder.encode(dateFormat.format(startTime), "UTF-8")
					+ "&end_time="
					+ URLEncoder.encode(dateFormat.format(now), "UTF-8"));
			ArrayList<LotCount> counts = getCounts(countsURI);
			Lot lot = LotData.getLots().get(lotID);
			lot.setCounts(countType, counts);
			
			Date endTime = getPredictionDate();
			URI predictionsURI = new URI(API.PREDICTIONS_URL + "?start_time="
					+ URLEncoder.encode(dateFormat.format(now), "UTF-8")
					+ "&end_time="
					+ URLEncoder.encode(dateFormat.format(endTime), "UTF-8"));
			ArrayList<LotCount> predictions = getCounts(predictionsURI);
			lot.setPredictions(countType, predictions);
			
		}
		catch (UnsupportedEncodingException e)
		{
			Log.e("WATPARK",
					"Error retrieving data from server " + e.getMessage());
		}
		catch (URISyntaxException e)
		{
			Log.e("WATPARK",
					"Error retrieving data from server " + e.getMessage());
		}

		updateChart();
		Session.getCountsRetrieving().get(lotID).remove(countType);
		Session.countUpdated(countType);
		return null;
	}

	private Date getPredictionDate()
	{
		Date now = new Date();
		if (countType == Lot.HOUR_COUNTS)
		{
			return new Date(now.getTime() + Util.HOUR_MS/2);
		}
		else if (countType == Lot.DAY_COUNTS)
		{
			return new Date(now.getTime() + Util.DAY_MS/Util.PREDICTION_RATIO);
		}
		else
		{
			return new Date(now.getTime() + Util.WEEK_MS/Util.PREDICTION_RATIO);
		}
	}
	
	private ArrayList<LotCount> getCounts(URI uri)
	{
		String response = getJSONResponse(uri);
		ArrayList<LotCount> counts = new ArrayList<LotCount>();
		JSONArray countArray = null;
		try
		{
			countArray = new JSONArray(response);
		}
		catch (JSONException jse)
		{
			Log.e("WATPARK", "Error retrieving JSON " + jse.getMessage());
			return counts;
		}
		catch (NullPointerException e)
		{
			Log.e("WATPARK", "Error retrieving JSON " + e.getMessage());
			return counts;
		}
		
		try
		{
			// Go through each item in the array
			for (int i = 0; i < countArray.length(); i++)
			{
				JSONObject countObject = countArray.getJSONObject(i);
				int count = countObject.getInt(API.CAR_COUNT_COLUMN);
				Date timePolled = dateFormat.parse(countObject
						.getString(API.TIME_POLLED_COLUMN));

				counts.add(new LotCount(timePolled, count));
			}
		}
		catch (Exception e)
		{
			Log.e("WATPARK",
					"Error parsing lot counts " + e.getMessage());
		}
		return counts;
	}

	private String getJSONResponse(URI updateURI)
	{

		try
		{
			HttpContext httpContext = new BasicHttpContext();
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(updateURI);
			HttpResponse response = httpClient.execute(httpGet, httpContext);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		}
		catch (Exception e)
		{
			Log.e("WATPARK",
					"Error retrieving data from server " + e.getMessage());
			Session.getCountsRetrieving().get(lotID).remove(countType);
			return null;
		}
	}
	
	private void updateChart()
	{
		Runnable r = new Runnable()
		{

			@Override
			public void run()
			{
				listener.updateChart(lotID, countType);
			}

		};

		countHandler.post(r);
	}
}
