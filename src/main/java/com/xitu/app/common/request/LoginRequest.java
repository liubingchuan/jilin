package com.xitu.app.common.request;

import com.xitu.app.common.BaseRequest;

public class LoginRequest extends BaseRequest{

	private String account;
	
	private String token;
	
	private String password;
	

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
}
