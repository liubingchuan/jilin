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

@Document(indexName = "expertyiyao", type = "et")
public class Expert implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	@CrossQuery
	private String name; // 姓名
	
	@Field(type=FieldType.Keyword)
	private List<String> alias; // 其他姓名
	
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private String unit; // 单位
	
	private String type; // 类型
	private String phone; // 手机
	private String tel; // 办公电话
	private String email; 
	
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private List<String> area; //研究领域

	private String address; // 地址
	
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private List<String> duty; // 职务
	
	@Field(type=FieldType.Keyword)
	private List<String> title; // 职称
	private String resume; // 简历
	@Field(type=FieldType.Keyword)
	private List<String> project; // 科研项目
	private String photo;   // 照片
	private String ctime;  // 提交时间
	private String uploader; // 提交人
	private Long now;
	private String frontend; // 专家照片
	private String frontendFileName; // 专家照片名
	private String frontendSize; // 专家照片大小
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
	public List<String> getAlias() {
		return alias;
	}
	public void setAlias(List<String> alias) {
		this.alias = alias;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getResume() {
		return resume;
	}
	public void setResume(String resume) {
		this.resume = resume;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
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
	public Long getNow() {
		return now;
	}
	public void setNow(Long now) {
		this.now = now;
	}
	public List<String> getArea() {
		return area;
	}
	public void setArea(List<String> area) {
		this.area = area;
	}
	public List<String> getDuty() {
		return duty;
	}
	public void setDuty(List<String> duty) {
		this.duty = duty;
	}
	public List<String> getTitle() {
		return title;
	}
	public void setTitle(List<String> title) {
		this.title = title;
	}
	public List<String> getProject() {
		return project;
	}
	public void setProject(List<String> project) {
		this.project = project;
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
