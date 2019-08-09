package com.xitu.app.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;

public final class HttpClientOperation {
	String endpoint ="http://tenant.env15.sg.com/";
	
	
   
	
    public String getEndpoint() {
		return endpoint;
	}


	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}


	// 发送请求
    public JSONObject doAction(HttpUriRequest request) throws IOException {
        HttpResponse httpResponse = null;
        try {
        	HttpClient httpClient = HttpClientBuilder.create().build();
            httpResponse = httpClient.execute(request);
            String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
           
            JSONObject resultJS = JSON.parseObject(result);
            return resultJS;
        } catch (JSONException e) {
            throw e;
        } catch (ClientProtocolException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }


    // 获取get请求
    public HttpGet buildHttpGet(String url, Map<String, Object> param) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException {
       
        param = new TreeMap<>(param);
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(endpoint);
        urlBuffer.append(url+"?");
        Object value;
        for (Entry<String, Object> entry : param.entrySet()) {
            value = entry.getValue();
            if (null != value) {
                urlBuffer.append(entry.getKey() + "=").append(value).append("&");
            }
        }
        String result = urlBuffer.substring(0, urlBuffer.length() - 1);  
        HttpGet httpget = new HttpGet(result);
        return httpget;
    }

    // 获取post请求
    public HttpPost buildHttpPost(String url, Map<String, Object> param, JSONObject jsonObj) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException {
    	param = new TreeMap<>(param);
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(endpoint);
        urlBuffer.append(url+"?");
        Object value;
        for (Entry<String, Object> entry : param.entrySet()) {
            value = entry.getValue();
            if (null != value) {
                urlBuffer.append(entry.getKey() + "=").append(value).append("&");
            }
        }
        String result = urlBuffer.substring(0, urlBuffer.length() - 1);  
        HttpPost httpPost = new HttpPost(result);
//        JSONObject jsonObj = new JSONObject();
//        for (Entry<String, Object> entry : bodyparam.entrySet()) {
//            if (null != entry.getValue()) {
//                if (entry.getValue() instanceof JSONObject) {
//                    jsonObj.put(entry.getKey(), (JSONObject) entry.getValue());
//                } else if (entry.getValue() instanceof Number) {
//                    jsonObj.put(entry.getKey(), (Number) entry.getValue());
//                } else {
//                    jsonObj.put(entry.getKey(), String.valueOf(entry.getValue()));
//                }
//            }
//        }
        //设置参数到请求对象中
        StringEntity entity = new StringEntity(jsonObj.toJSONString(), Charset.forName("utf-8"));
        entity.setContentEncoding("utf-8");
        // 发送Json格式的数据请求
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        return httpPost;
    }

  
}
