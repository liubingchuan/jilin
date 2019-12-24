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

@Document(indexName = "patentjilin", type = "pt")
public class Patent implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	@CrossQuery
	private String title;  //标题 （天地锁）   En title
	@CrossQuery
	private String subject; //摘要 
	@CrossQuery
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private List<String> person;   // 专利权人（申请人）
	@AggQuery
	@Field(type=FieldType.Keyword)
	private List<String> applicantipc;   // applicant+_+ipc 的值存入该字段；（专利权人和ipc）
	@CrossQuery
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private List<String> creator;
	
	@AggQuery
	@Field(type=FieldType.Keyword)
	private List<String> ipcyear; // 将 ipc+_+applyyear 的值存入该字段
	@AggQuery
	@Field(type=FieldType.Keyword)
	private String month; // 提取自申请日
	@Field(type=FieldType.Keyword)
	private String applytime; // 申请日
	@Field(type=FieldType.Keyword)
	private String publictime; // 公开（公告）日   En Publication date
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private String applyyear; // 申请年
	
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private String publicyear; // 公开年
	@AggQuery
	@Field(type=FieldType.Keyword)
	private String typeyear; // typeyear
	@AggQuery
	@Field(type=FieldType.Keyword)
	private String typemonth; // typemonth
	@AggQuery
	@Field(type=FieldType.Keyword)
	private String type; // 专利类型
	private String description; // 专利描述
	@Field(type=FieldType.Keyword)
	private String claim; //权利要求 （主权项）
	@Field(type=FieldType.Keyword)
	private String publicnumber; //公开号
	@AggQuery
	@Field(type=FieldType.Keyword)
	private String applynumber; //申请号
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private List<String> ipc; // ipc (分类号)
	
	@Field(type=FieldType.Keyword)
	private List<String> cpc; // cpc
	@Field(type=FieldType.Keyword)
	private String piroryear; //优先权年
	
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private String country; //国家
	
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private String lawstatus; // 法律状态
	private Long now;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public List<String> getPerson() {
		return person;
	}
	public void setPerson(List<String> person) {
		this.person = person;
	}
	public List<String> getCreator() {
		return creator;
	}
	public void setCreator(List<String> creator) {
		this.creator = creator;
	}
	public String getApplytime() {
		return applytime;
	}
	public void setApplytime(String applytime) {
		this.applytime = applytime;
	}
	public String getPublictime() {
		return publictime;
	}
	public void setPublictime(String publictime) {
		this.publictime = publictime;
	}
	public String getApplyyear() {
		return applyyear;
	}
	public void setApplyyear(String applyyear) {
		this.applyyear = applyyear;
	}
	public String getPublicyear() {
		return publicyear;
	}
	public void setPublicyear(String publicyear) {
		this.publicyear = publicyear;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getClaim() {
		return claim;
	}
	public void setClaim(String claim) {
		this.claim = claim;
	}
	public String getPublicnumber() {
		return publicnumber;
	}
	public void setPublicnumber(String publicnumber) {
		this.publicnumber = publicnumber;
	}
	public String getApplynumber() {
		return applynumber;
	}
	public void setApplynumber(String applynumber) {
		this.applynumber = applynumber;
	}
	public String getPiroryear() {
		return piroryear;
	}
	public void setPiroryear(String piroryear) {
		this.piroryear = piroryear;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Long getNow() {
		return now;
	}
	public void setNow(Long now) {
		this.now = now;
	}
	public List<String> getIpc() {
		return ipc;
	}
	public void setIpc(List<String> ipc) {
		this.ipc = ipc;
	}
	public List<String> getCpc() {
		return cpc;
	}
	public void setCpc(List<String> cpc) {
		this.cpc = cpc;
	}
	public String getLawstatus() {
		return lawstatus;
	}
	public void setLawstatus(String lawstatus) {
		this.lawstatus = lawstatus;
	}
	public List<String> getApplicantipc() {
		return applicantipc;
	}
	public void setApplicantipc(List<String> applicantipc) {
		this.applicantipc = applicantipc;
	}
	public List<String> getIpcyear() {
		return ipcyear;
	}
	public void setIpcyear(List<String> ipcyear) {
		this.ipcyear = ipcyear;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getTypeyear() {
		return typeyear;
	}
	public void setTypeyear(String typeyear) {
		this.typeyear = typeyear;
	}
	public String getTypemonth() {
		return typemonth;
	}
	public void setTypemonth(String typemonth) {
		this.typemonth = typemonth;
	}
	
	
}
