package com.xitu.app.model;

import com.xitu.app.annotation.IdFlag;
import com.xitu.app.annotation.TableName;

@TableName("yiyao_relation")
public class Relation {

	public Relation() {
		
	}
	
	@IdFlag
	private Integer id;
	private String orderId;
	private String linkuserId;
	private Integer tag;
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
