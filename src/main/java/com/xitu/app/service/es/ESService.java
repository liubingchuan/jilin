package com.xitu.app.service.es;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public interface ESService {

	void execute(int pageIndex, int pageSize, int type, String...args);
	JSONObject executeIns(String insNamearr,int pageIndex, int pageSize, String field, int type);
	JSONObject executeXiangguan(int pageIndex, int pageSize,int type,String uuid,List<String> args);
	void executefamingren(int pageIndex, int pageSize, int type,String q,String person,String creator);
}
