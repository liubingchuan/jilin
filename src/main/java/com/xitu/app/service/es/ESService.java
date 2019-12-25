package com.xitu.app.service.es;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public interface ESService {

	JSONObject execute(int pageIndex, int pageSize, int type, String...args);
	JSONObject executeIns(String insNamearr,int pageIndex, int pageSize, String field, int type);
	JSONObject executeXiangguan(int pageIndex, int pageSize,int type,String uuid,List<String> args);
	void executefamingren(int pageIndex, int pageSize, int type,String q,String person,String creator);
	JSONObject executeOneFiled(String filed,String value);
	int[] executeMonth(int pageIndex, int pageSize, int type, String...args);
	JSONObject executeTypeMonth(int pageIndex, int pageSize, int type, String...args);
	Map executeQushi(int pageIndex, int pageSize, int type, String q,String[] args1);
	Map executeJishufenbu(int pageIndex, int pageSize, int type, String q,String[] args1,String[] args2);
}
