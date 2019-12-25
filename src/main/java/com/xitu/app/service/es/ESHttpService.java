package com.xitu.app.service.es;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public interface ESHttpService extends ESService {

    ESHttpClient getHttpClient();

    String composeDSL(int pageIndex, int pageSize, int type, String[] args);
    String composeInsDSL(String insNamearr,int pageIndex, int pageSize, String field, int type);
    String composeXiangguanDSL(int pageIndex, int pageSize,int type,String uuid,List<String> args);
    String composefamingrenDSL(int pageIndex, int pageSize,int type,String q,String person,String creator);
    JSONObject convert(JSONObject response);
    JSONObject convertIns(JSONObject response,int pageSize);
    int[] convertMonth(JSONObject response);
    Map convertQushi(JSONObject response);
}
