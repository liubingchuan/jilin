package com.xitu.app.common.request;

import com.xitu.app.common.BaseRequest;

public class SaveExpertRequest extends BaseRequest{

	private String id;
	private String unit; // 单位
	private String type; // 类型
	private String phone; // 手机
	private String tel; // 办公电话
	private String email; 
	private String area; //研究领域
	private String address; // 地址
	private String duty; // 职务
	private String title; // 职称
	private String resume; // 简历
	private String project; // 科研项目
	private String ctime;  // 提交时间
	private String uploader; // 提交人
	private String info;
	private String name;
	private String alias;
	private String frontend; // 专家照片
	private String frontendFileName; // 专家照片文件名
	private String frontendSize; // 专家照片大小
	
	
	
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
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDuty() {
		return duty;
	}
	public void setDuty(String duty) {
		this.duty = duty;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getResume() {
		return resume;
	}
	public void setResume(String resume) {
		this.resume = resume;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
	public String getUploader() {
		return uploader;
	}
	public void setUploader(String uploader) {
		this.uploader = uploader;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
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
