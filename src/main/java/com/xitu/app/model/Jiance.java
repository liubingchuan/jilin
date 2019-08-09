package com.xitu.app.model;

import java.io.Serializable;
import java.util.List;

import com.xitu.app.annotation.AggQuery;
import com.xitu.app.annotation.CrossQuery;
import com.xitu.app.annotation.SingleQuery;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "jianceyiyao", type = "jc")
public class Jiance implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	@CrossQuery
	private String title;  //标题
	@CrossQuery
	private String description; //正文
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private String institution;//来源机构
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private String lanmu;//栏目
	@Field(type=FieldType.Keyword)
	private String pubtime;//发表日期
	@Field(type=FieldType.Keyword)
	private String pubyear;//发表年
	@Field(type=FieldType.Keyword)
	private String link; //原文地址
	@Field(type=FieldType.Keyword)
	private List<String> fenlei;
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
