package com.xitu.app.model;

import com.xitu.app.annotation.IdFlag;
import com.xitu.app.annotation.TableName;

@TableName("yiyao_patent")
public class PatentMysql {

	public PatentMysql() {
		
	}
	
	@IdFlag
	private Integer id;
	private String title;  //标题 （天地锁）
	private String subject; //摘要 
	private String person;   // 专利权人（申请人）
	private String creator;
	private String applytime; // 申请日
	private String publictime; // 公开（公告）日
	private String applyyear; // 申请年
	private String publicyear; // 公开年
	private String ptype; // 专利类型
	private String description; // 专利描述
	private String claim; //权利要求 （主权项）
	private String publicnumber; //公开号
	private String applynumber; //申请号
	private String ipc; // ipc (分类号)
	private String cpc; // cpc
	private String piroryear; //优先权年
	private String country; //国家
	private String lawstatus; // 法律状态
	private Long now;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
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
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
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
	public String getIpc() {
		return ipc;
	}
	public void setIpc(String ipc) {
		this.ipc = ipc;
	}
	public String getCpc() {
		return cpc;
	}
	public void setCpc(String cpc) {
		this.cpc = cpc;
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
	public String getLawstatus() {
		return lawstatus;
	}
	public void setLawstatus(String lawstatus) {
		this.lawstatus = lawstatus;
	}
	public Long getNow() {
		return now;
	}
	public void setNow(Long now) {
		this.now = now;
	}
	public String getPtype() {
		return ptype;
	}
	public void setPtype(String ptype) {
		this.ptype = ptype;
	}
	
	

}
