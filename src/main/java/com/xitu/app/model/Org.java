package com.xitu.app.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.xitu.app.annotation.AggQuery;
import com.xitu.app.annotation.CrossQuery;
import com.xitu.app.annotation.SingleQuery;

@Document(indexName = "orgyiyao", type = "og")
public class Org implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	
	@CrossQuery
	private String name; // 机构名称
	
	@Field(type=FieldType.Keyword)
	private List<String> alias; // 其他名称
	
	private String description; // 机构介绍
	@Field(type=FieldType.Keyword)
	private List<String> area;  // 研究领域 
	
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private String type;  // 机构类型
	
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private String link; // 产业链
	
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private List<String> classic; // 产品类型
	
	private String country; // 国家
	private String logo; // 机构logo
	private String professors; // 专家数量
	private String patents;   // 专利数量
	private String dymanics;   // 动态数量
	private String basic;  // 基本介绍
	private String friends;  // 相关专家
	private String ap; // 相关专利
	private String paper; // 相关论文
	private String message; // 相关动态
	private String ctime; // 提交时间
	private Long now; // 更改时间
	private String frontend; // LOGO封面
	private String frontendFileName; // LOGO文件名
	private String frontendSize; // LOGO大小
	
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
	public Long getNow() {
		return now;
	}
	public void setNow(Long now) {
		this.now = now;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<String> getAlias() {
		return alias;
	}
	public void setAlias(List<String> alias) {
		this.alias = alias;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getProfessors() {
		return professors;
	}
	public void setProfessors(String professors) {
		this.professors = professors;
	}
	public String getPatents() {
		return patents;
	}
	public void setPatents(String patents) {
		this.patents = patents;
	}
	public String getDymanics() {
		return dymanics;
	}
	public void setDymanics(String dymanics) {
		this.dymanics = dymanics;
	}
	public String getBasic() {
		return basic;
	}
	public void setBasic(String basic) {
		this.basic = basic;
	}
	public String getFriends() {
		return friends;
	}
	public void setFriends(String friends) {
		this.friends = friends;
	}
	public String getAp() {
		return ap;
	}
	public void setAp(String ap) {
		this.ap = ap;
	}
	public String getPaper() {
		return paper;
	}
	public void setPaper(String paper) {
		this.paper = paper;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<String> getArea() {
		return area;
	}
	public void setArea(List<String> area) {
		this.area = area;
	}
	public List<String> getClassic() {
		return classic;
	}
	public void setClassic(List<String> classic) {
		this.classic = classic;
	}
	public String getFrontend() {
		return frontend;
	}
	public void setFrontend(String frontend) {
		this.frontend = frontend;
	}
	public String getFrontendFileName() {
		return frontendFileName;
	}
	public void setFrontendFileName(String frontendFileName) {
		this.frontendFileName = frontendFileName;
	}
	public String getFrontendSize() {
		return frontendSize;
	}
	public void setFrontendSize(String frontendSize) {
		this.frontendSize = frontendSize;
	}
	
	
}
