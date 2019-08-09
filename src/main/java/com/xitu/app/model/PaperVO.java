package com.xitu.app.model;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class PaperVO {
	private String _id;
	private String url;
	private String title;
	@JSONField(name = "online_date")
	private String onlineDate;
	private List<String> author;
	@JSONField(name = "abstract")
	private String abc;
	@JSONField(name = "paper_type")
	private String paperType;
	@JSONField(name = "key_word")
	private List<String> keyWord;
	@JSONField(name = "search_word")
	private String searchWord;
	@JSONField(name = "organization")
	private List<String> organization;
	private List<String> region;
	private String journal;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOnlineDate() {
		return onlineDate;
	}

	public void setOnlineDate(String onlineDate) {
		this.onlineDate = onlineDate;
	}

	public List<String> getAuthor() {
		return author;
	}

	public void setAuthor(List<String> author) {
		this.author = author;
	}

	public String getAbc() {
		return abc;
	}

	public void setAbc(String abc) {
		this.abc = abc;
	}

	public String getPaperType() {
		return paperType;
	}

	public void setPaperType(String paperType) {
		this.paperType = paperType;
	}


	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public String getJournal() {
		return journal;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}

	public List<String> getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(List<String> keyWord) {
		this.keyWord = keyWord;
	}

	public List<String> getOrganization() {
		return organization;
	}

	public void setOrganization(List<String> organization) {
		this.organization = organization;
	}

	public List<String> getRegion() {
		return region;
	}

	public void setRegion(List<String> region) {
		this.region = region;
	}

	
}
