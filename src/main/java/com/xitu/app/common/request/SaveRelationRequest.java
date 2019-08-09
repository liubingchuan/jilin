package com.xitu.app.common.request;

import java.util.List;

import com.xitu.app.common.BaseRequest;

public class SaveRelationRequest extends BaseRequest{

	private Integer id;
	private String orderId;
	private String linkuserId;
	private Integer tag;
	private String token;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getLinkuserId() {
		return linkuserId;
	}
	public void setLinkuserId(String linkuserId) {
		this.linkuserId = linkuserId;
	}
	public Integer getTag() {
		return tag;
	}
	public void setTag(Integer tag) {
		this.tag = tag;
	}
	
}
