package com.xitu.app.common.request;

import com.xitu.app.common.BaseRequest;

public class PriceAvgRequest extends BaseRequest{

	private String time;
	
	private String name;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
