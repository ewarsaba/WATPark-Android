package com.watpark.android.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Lot implements Serializable {

	private static final long serialVersionUID = 6083051709504852540L;
	private String name;
	private int capacity = -1;
	private Date openTime = null;
	private Date closeTime = null;
	private double latitude = -1;
	private double longitude = -1;
	private Date latestTimePolled = null;
	private int latestCount = -1;
	private String status;
	private HashMap<Integer, ArrayList<LotCount>> counts = new HashMap<Integer, ArrayList<LotCount>>();
	private HashMap<Integer, ArrayList<LotCount>> predictions = new HashMap<Integer, ArrayList<LotCount>>();
	private int id = -1;
	public static final int HOUR_COUNTS = 0;
	public static final int DAY_COUNTS = 1;
	public static final int WEEK_COUNTS = 2;
	public static final String ONLINE_STATUS = "";
	public static final String NODATA_STATUS = "nodata";
	public static final String MAINTENANCE_STATUS = "maintenance";
	
	public Lot(int id, String name, int capacity) {
		this.id = id;
		this.name = name;
		this.capacity = capacity;
	}

	public int getID() {
		return this.id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}

	public Date getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Date getLatestTimePolled() {
		return latestTimePolled;
	}

	public void setLatestTimePolled(Date latestTimePolled) {
		this.latestTimePolled = latestTimePolled;
	}

	public int getLatestCount() {
		return latestCount;
	}

	public void setLatestCount(int latestCount) {
		this.latestCount = latestCount;
	}
	
	public void setCounts(int type, ArrayList<LotCount> counts) {
		this.counts.put(type, counts);
	}
	
	public ArrayList<LotCount> getCounts(int type) {
		return counts.get(type);
	}
	
	public void setPredictions(int type, ArrayList<LotCount> predictions) {
		this.predictions.put(type, predictions);
	}
	
	public ArrayList<LotCount> getPredictions(int type) {
		return predictions.get(type);
	}
}
