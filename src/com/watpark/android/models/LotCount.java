package com.watpark.android.models;

import java.util.Date;

public class LotCount {
	private Date timePolled;
	private int count;
	
	public LotCount(Date timePolled, int count) {
		this.timePolled = timePolled;
		this.count = count;
	}

	public Date getTimePolled() {
		return timePolled;
	}

	public void setTimePolled(Date timePolled) {
		this.timePolled = timePolled;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	
}
