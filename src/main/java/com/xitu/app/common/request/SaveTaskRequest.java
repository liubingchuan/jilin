package com.xitu.app.common.request;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.xitu.app.common.BaseRequest;

public class SaveTaskRequest extends BaseRequest{

	private String id;
	private String title;  //标题
	private String description; //正文
	private String institution;//来源机构
	private String lanmu;//栏目
	private String pubtime;//发表日期
	private String pubyear;//发表年
	private String link; //原文地址
	private List<String> fenlei;
	private String info;
	
	
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getInstitution() {
		return institution;
	}
	public void setInstitution(String institution) {
		this.institution = institution;
	}
	public String getLanmu() {
		return lanmu;
	}
	public void setLanmu(String lanmu) {
		this.lanmu = lanmu;
	}
	public String getPubtime() {
		return pubtime;
	}
	public void setPubtime(String pubtime) {
		this.pubtime = pubtime;
	}
	public String getPubyear() {
		return pubyear;
	}
	public void setPubyear(String pubyear) {
		this.pubyear = pubyear;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public List<String> getFenlei() {
		return fenlei;
	}
	public void setFenlei(List<String> fenlei) {
		this.fenlei = fenlei;
	}
	
}
