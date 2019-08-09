package com.xitu.app.common.request;

import com.xitu.app.common.BaseRequest;

public class GetUserInfoRequest extends BaseRequest{

	private String account;
	
	private String type;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
