package com.xitu.app.common.request;

import com.xitu.app.common.BaseRequest;

public class SaveReportRequest extends BaseRequest{

	private String id;
	private String name; // 报告名称
	private String subject; // 报告摘要
	private String author; // 报告作者
	private String unit;  // 完成单位 
	private String ptime;  // 发布时间
	private String type; // 报告分类
	private String frontend; // 报告封面
	private String frontendFileName; // 报告封面文件名
	private String frontendSize; // 报告封面大小
	private String pdf; // pdf 简报
	private String pdfFileName;
	private String pdfSize;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getPtime() {
		return ptime;
	}
	public void setPtime(String ptime) {
		this.ptime = ptime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFrontend() {
		return frontend;
	}
	public void setFrontend(String frontend) {
		this.frontend = frontend;
	}
	public String getPdf() {
		return pdf;
	}
	public void setPdf(String pdf) {
		this.pdf = pdf;
	}
	public Long getNow() {
		return now;
	}
	public void setNow(Long now) {
		this.now = now;
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
	public String getPdfFileName() {
		return pdfFileName;
	}
	public void setPdfFileName(String pdfFileName) {
		this.pdfFileName = pdfFileName;
	}
	public String getPdfSize() {
		return pdfSize;
	}
	public void setPdfSize(String pdfSize) {
		this.pdfSize = pdfSize;
	}
	
	
	
	
}
