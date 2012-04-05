package com.watpark.android;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import android.app.Activity;

import com.watpark.android.activities.SplashActivity;


/* Current global status for app */
public class Session
{
	private static Object listenersLock = new Object();
	private static boolean updatesRunning = false;
	private static HashMap<Integer, HashSet<Integer>> countsRetrieving = new HashMap<Integer, HashSet<Integer>>();
	private static long[] countsUpdated = { 0, 0, 0};
	private static HashSet<LotUpdateListener> listeners = new HashSet<LotUpdateListener>();
	private static HashSet<Activity> currentActivities = new HashSet<Activity>();
	
	public static boolean isUpdateRunning()
	{
		return updatesRunning;
	}
	
	public static void setUpdateRunning(boolean updateRunning)
	{
		Session.updatesRunning = updateRunning;
	}

	public static long getCountsLastUpdated(int countType)
	{
		return countsUpdated[countType];
	}

	public static void addActivity(Activity activity)
	{
		if (!(activity instanceof SplashActivity))
		{
			currentActivities.add(activity);
		}
	}
	
	public static void removeActivity(Activity activity)
	{
		currentActivities.remove(activity);
	}
	
	public static void finishActivities()
	{
		Activity[] activities = new Activity[currentActivities.size()];
		activities = currentActivities.toArray(activities);
		
		for (Activity a : activities)
		{
			a.finish();
		}
	}
	
	public static void addListener(LotUpdateListener listener)
	{
		synchronized (listenersLock)
		{
			listeners.add(listener);

			if (!updatesRunning)
			{
				new UpdateTask().execute();
			}
		}

	}

	public static void removeListener(LotUpdateListener listener)
	{
		synchronized (listenersLock)
		{
			listeners.remove(listener);
		}
	}

	public static HashSet<LotUpdateListener> getUpdateListeners()
	{
		return listeners;
	}
	
	public static Object getUpdateListenersLock()
	{
		return listenersLock;
	}
	
	public static void retrieveLotCounts(Date startTime, int countType,
			int lotID, LotChartListener listener)
	{
		HashSet<Integer> retrieving = countsRetrieving.get(lotID);
		if (retrieving == null)
		{
			retrieving = new HashSet<Integer>();
			countsRetrieving.put(lotID, retrieving);
		}
		if (!retrieving.contains(countType))
		{
			CountsTask task = new CountsTask();
			task.initialize(startTime, countType, lotID, listener);
			task.execute();
			retrieving.add(countType);
		}
	}
	
	public static HashMap<Integer, HashSet<Integer>> getCountsRetrieving()
	{
		return countsRetrieving;
	}
	
	public static void countUpdated(int countType)
	{
		countsUpdated[countType] = new Date().getTime();
	}
}
