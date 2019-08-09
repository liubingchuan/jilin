package com.xitu.app.common.request;

import com.xitu.app.common.BaseRequest;

public class SaveOrgRequest extends BaseRequest{

	private String id;
	private String name; // 机构名称
	private String alias; // 其他名称
	private String description; // 机构介绍
	private String area;  // 研究领域 
	private String type;  // 机构类型
	private String country; // 国家
	private String link; // 产业链
	private String classic; // 产品类型
	private String frontend; // logo
	private String frontendFileName; // logo文件名
	private String frontendSize; // logo大小
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
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
	public String getClassic() {
		return classic;
	}
	public void setClassic(String classic) {
		this.classic = classic;
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
