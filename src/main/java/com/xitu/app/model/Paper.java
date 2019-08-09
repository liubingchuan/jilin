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

@Document(indexName = "paperyiyao", type = "pr")
public class Paper implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	@CrossQuery
	private String title;  //标题
	@CrossQuery
	private String subject; //摘要
	
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private List<String> author;   // 作者
	
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private List<String> institution; // 机构
	
	@Field(type=FieldType.Keyword)
	private List<String> keywords; // 关键词
	
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private String journal; // 期刊（出处）
	
	@Field(type=FieldType.Keyword)
	private String volume; // 卷
	
	@Field(type=FieldType.Keyword)
	private String issue; // 期
	
	@Field(type=FieldType.Keyword)
	private String page; // 页码 //去掉
	
	@Field(type=FieldType.Keyword)
	private String issn; // issn
	
	@AggQuery
	@SingleQuery
	@Field(type=FieldType.Keyword)
	private String year; // 年
	
	@Field(type=FieldType.Keyword)
	private List<String> link; // 全文链接
	
	@Field(type=FieldType.Keyword)
	private String citenum; // 被引量
	
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

	public List<String> getAuthor() {
		return author;
	}

	public void setAuthor(List<String> author) {
		this.author = author;
	}

	public List<String> getInstitution() {
		return institution;
	}

	public void setInstitution(List<String> institution) {
		this.institution = institution;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public String getJournal() {
		return journal;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getIssn() {
		return issn;
	}

	public void setIssn(String issn) {
		this.issn = issn;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public List<String> getLink() {
		return link;
	}

	public void setLink(List<String> link) {
		this.link = link;
	}

	public String getCitenum() {
		return citenum;
	}

	public void setCitenum(String citenum) {
		this.citenum = citenum;
	}

	public Long getNow() {
		return now;
	}

	public void setNow(Long now) {
		this.now = now;
	}
	
	
	
}
