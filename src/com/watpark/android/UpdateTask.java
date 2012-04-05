package com.watpark.android;

import java.net.URI;
import java.text.SimpleDateFormat;

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


/* Task that polls the server for updates on the lots */
public class UpdateTask extends AsyncTask<Void, Void, Void>
{
	private static Handler lotHandler = new Handler();
	
	@Override
	protected Void doInBackground(Void... params)
	{

		Session.setUpdateRunning(true);
		
		while (true)
		{
			String response = "";

			try
			{
				// connect to server
				URI updateURI = new URI(API.LOT_URL);
				HttpContext httpContext = new BasicHttpContext();
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(updateURI);
				HttpResponse resp = httpClient.execute(httpGet, httpContext);
				HttpEntity entity = resp.getEntity();
				response = EntityUtils.toString(entity);
			}
			catch (Exception e)
			{
				Log.e("WATPARK",
						"Error retrieving data from server "
								+ e.getMessage());
				sleep(1000);
				continue;
			}

			try
			{
				JSONArray lotArray = new JSONArray(response);

				parseLotJSON(lotArray);
			}
			catch (JSONException jse)
			{
				Log.e("WATPARK",
						"Error retrieving JSON " + jse.getMessage());
			}

			sleep(10000);

			synchronized (Session.getUpdateListenersLock())
			{
				if (Session.getUpdateListeners().size() == 0)
				{
					break;
				}
			}
		}

		Session.setUpdateRunning(false);
		return null;
	}

	private void parseLotJSON(JSONArray lotArray)
	{
		try
		{
			// Go through each item in the array
			for (int i = 0; i < lotArray.length(); i++)
			{
				// initialize lot info
				JSONObject lotJSON = lotArray.getJSONObject(i);
				int lotID = lotJSON.getInt(API.ID_COLUMN);
				Lot lot = LotData.getLots().get(lotID);

				if (lot == null)
				{
					lot = new Lot(lotJSON.getInt(API.ID_COLUMN),
							lotJSON.getString(API.NAME_COLUMN),
							lotJSON.getInt(API.CAPACITY_COLUMN));
					lot.setStatus(lotJSON.getString(API.STATUS_COLUMN));
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"HH:mm:ss");
					lot.setOpenTime(dateFormat.parse(lotJSON
							.getString(API.OPEN_TIME_COLUMN)));
					lot.setCloseTime(dateFormat.parse(lotJSON
							.getString(API.CLOSE_TIME_COLUMN)));
					dateFormat = new SimpleDateFormat(API.DATE_FORMAT_COUNT);
					JSONArray latLon = new JSONArray(
							lotJSON.getString(API.POSITION_COLUMN));
					double latitude = latLon.getDouble(0);
					double longitude = latLon.getDouble(1);
					lot.setLatitude(latitude);
					lot.setLongitude(longitude);

					// put in hash map
					LotData.getLots().put(lot.getID(), lot);
				}

				if (lotJSON.has(API.LOT_COUNT_COLUMN)
						&& !lotJSON.getString(API.LOT_COUNT_COLUMN).equals(
								"0"))
				{
					lot.setLatestCount(lotJSON.getJSONObject(
							API.LOT_COUNT_COLUMN).getInt(
							API.CAR_COUNT_COLUMN));
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							API.DATE_FORMAT_COUNT);
					lot.setLatestTimePolled(dateFormat.parse(lotJSON
							.getJSONObject(API.LOT_COUNT_COLUMN).getString(
									API.TIME_POLLED_COLUMN)));

				}

			}

			Runnable r = new Runnable()
			{

				@Override
				public void run()
				{
					synchronized (Session.getUpdateListenersLock())
					{
						for (LotUpdateListener listener : Session
								.getUpdateListeners())
						{
							listener.updateLotInfo();
						}
					}
				}
			};

			lotHandler.post(r);

		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}

	private void sleep(long pollTime)
	{
		try
		{
			Thread.sleep(pollTime);
		}
		catch (InterruptedException ie)
		{
			Log.e("WATPARK", "Error on thread sleep: " + ie.getMessage());
		}
	}
};