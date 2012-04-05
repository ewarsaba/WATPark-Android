package com.watpark.android;

import java.util.LinkedHashMap;

import com.watpark.android.models.Lot;

public class LotData
{
	private static LinkedHashMap<Integer, Lot> lots = new LinkedHashMap<Integer, Lot>();
	
	public static void setLots(LinkedHashMap<Integer, Lot> lots)
	{
		LotData.lots = lots;
	}

	public static LinkedHashMap<Integer, Lot> getLots()
	{
		return LotData.lots;
	}
}
