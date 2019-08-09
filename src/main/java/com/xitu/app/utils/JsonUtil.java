package com.xitu.app.utils;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtil {

    public static String toJSONString(Object obj) {
        return JSON.toJSONString(obj, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero);
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz);
    }

    public static String toJSONString(Object obj, boolean prettyFormat) {
        return JSON.toJSONString(obj, prettyFormat);
    }
    
    public static JSONObject parseObject(String text) {
        JSONObject jsonObject = JSON.parseObject(text);
        return jsonObject;
    }

    public static JSONArray parseArray(String text) {
        JSONArray jsonArray = JSON.parseArray(text);
        return jsonArray;
    }

    public static String prettyFormat(Object object) {
        return toJSONString(object, true);
    }

    public static String prettyJsonText(String text) {
        return toJSONString(JSON.parse(text), true);
    }
    
  
	

    public static void main(String[] args) {
        String text = "{'code':200,'data':{'total':26,'region_no':'cn-hangzhou-dg-a01','zones':[{'disk_type':['dfs1','dfs2'],'route_ip_segment':'110.75.188.0/23;110.76.44.0/22;112.127.0.0/18;112.127.128.0/18;112.127.64.0/18;121.198.128.0/19;223.4.0.0/20;223.6.248.0/21;42.120.0.0/22;42.120.61.0/24','zone_no':'cn-hangzhou-1-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'112.127.0.0/18;112.127.128.0/18;112.127.64.0/18;121.198.128.0/19;223.4.32.0/20;223.6.248.0/21;42.120.16.0/22;42.120.20.0/22;42.121.136.0/22','zone_no':'cn-hangzhou-1-b'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'112.124.160.0/19;121.197.112.0/20;121.197.16.0/20;121.197.32.0/20;121.197.48.0/20;121.197.64.0/19;121.197.96.0/20;121.198.160.0/19;121.198.192.0/18;223.4.144.0/21;223.4.248.0/22;42.121.0.0/22;42.121.104.0/22;42.121.112.0/22;42.121.96.0/22','zone_no':'cn-hangzhou-dg101-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'112.124.160.0/19;121.197.112.0/20;121.197.16.0/20;121.197.32.0/20;121.197.48.0/20;121.197.64.0/19;121.197.96.0/20;121.198.160.0/19;121.198.192.0/18;223.4.152.0/21;42.121.116.0/22;42.121.120.0/21;42.121.4.0/22','zone_no':'cn-hangzhou-dg101-b'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'112.124.160.0/19;121.197.112.0/20;121.197.16.0/20;121.197.32.0/20;121.197.48.0/20;121.197.64.0/19;121.197.96.0/20;121.198.0.0/18;121.198.160.0/19;121.198.192.0/18;121.198.64.0/18;223.4.168.0/21;223.4.232.0/21;42.121.12.0/22;42.121.236.0/22;42.121.64.0/22;42.121.68.0/22','zone_no':'cn-hangzhou-dg104-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'112.124.160.0/19;121.197.112.0/20;121.197.16.0/20;121.197.32.0/20;121.197.48.0/20;121.197.64.0/19;121.197.96.0/20;121.198.0.0/18;121.198.160.0/19;121.198.192.0/18;121.198.64.0/18;223.4.176.0/21;223.4.240.0/21;42.121.16.0/22;42.121.80.0/21;42.121.88.0/22','zone_no':'cn-hangzhou-dg104-b'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'112.124.160.0/19;121.197.0.0/21;121.197.112.0/20;121.197.16.0/20;121.197.32.0/20;121.197.48.0/20;121.197.64.0/19;121.197.96.0/20;121.198.192.0/18;223.4.200.0/21;42.121.128.0/22;42.121.28.0/22;42.121.52.0/22;42.121.56.0/22','zone_no':'cn-hangzhou-dg108-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'112.124.160.0/19;121.197.112.0/20;121.197.16.0/20;121.197.32.0/20;121.197.48.0/20;121.197.64.0/19;121.197.8.0/21;121.197.96.0/20;121.198.192.0/18;223.4.208.0/21;42.121.108.0/22;42.121.132.0/22;42.121.32.0/22;42.121.76.0/22','zone_no':'cn-hangzhou-dg108-b'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'223.4.48.0/20;223.6.248.0/21;42.121.140.0/22','zone_no':'cn-hangzhou-dg4-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'223.4.216.0/22;223.4.96.0/22;223.6.248.0/21;42.120.4.0/22;42.121.144.0/22;42.121.192.0/22','zone_no':'cn-hangzhou-dg4-b'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'110.76.38.0/23;110.76.40.0/22;112.127.0.0/18;112.127.128.0/18;112.127.64.0/18;121.198.128.0/19;223.4.80.0/20;42.120.32.0/22;42.120.61.0/24;42.120.63.0/24','zone_no':'cn-hangzhou-dg6-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'112.127.0.0/18;112.127.128.0/18;112.127.64.0/18;121.198.128.0/19;223.4.16.0/20;223.6.0.0/16;223.7.0.0/16;42.120.12.0/22;42.120.60.0/24;42.120.8.0/22','zone_no':'cn-hangzhou-dg6-b'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'112.127.0.0/18;112.127.128.0/18;112.127.64.0/18;121.198.128.0/19;223.4.112.0/20;223.6.248.0/21;42.120.40.0/22;42.120.44.0/22;42.120.63.0/24','zone_no':'cn-hangzhou-dg8-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'112.127.0.0/18;112.127.128.0/18;112.127.64.0/18;121.198.128.0/19;223.4.128.0/20;223.6.248.0/21;42.120.48.0/22;42.120.52.0/22','zone_no':'cn-hangzhou-dg8-b'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'115.29.160.0/22;115.29.164.0/22;115.29.184.0/22;115.29.220.0/22;115.29.228.0/22;115.29.232.0/22;115.29.236.0/22','zone_no':'cn-hangzhou-et1001-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'115.29.168.0/22;115.29.172.0/22;115.29.176.0/22;115.29.188.0/22;115.29.192.0/22;115.29.224.0/22;115.29.240.0/22','zone_no':'cn-hangzhou-et1002-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'115.29.180.0/22','zone_no':'cn-hangzhou-et1003-a'},{'disk_type':['io1'],'route_ip_segment':'115.29.212.0/22;115.29.216.0/22','zone_no':'cn-hangzhou-et1005-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'112.124.176.0/20;121.199.0.0/22;121.199.12.0/22;121.199.16.0/22;121.199.20.0/22;121.199.4.0/22;121.199.48.0/22;121.199.52.0/22;121.199.8.0/22','zone_no':'cn-hangzhou-gy001-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'112.124.176.0/20;121.199.24.0/22;121.199.28.0/22;121.199.32.0/22;121.199.36.0/22;121.199.40.0/22;121.199.44.0/22;121.199.56.0/22;121.199.60.0/22','zone_no':'cn-hangzhou-gy002-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'121.199.128.0/22','zone_no':'cn-hangzhou-gy003-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'121.199.160.0/22;121.199.164.0/22;121.199.168.0/22;121.199.172.0/22;121.199.176.0/22;121.199.180.0/22','zone_no':'cn-hangzhou-gy004-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'112.124.0.0/22;112.124.12.0/22;112.124.16.0/22;112.124.20.0/22;112.124.24.0/22;112.124.28.0/22;112.124.4.0/22;112.124.64.0/22;112.124.8.0/22','zone_no':'cn-hangzhou-gy005-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'112.124.32.0/22;112.124.36.0/22;112.124.40.0/22;112.124.44.0/22;112.124.48.0/22;112.124.52.0/22;112.124.56.0/22;112.124.60.0/22;112.124.68.0/22','zone_no':'cn-hangzhou-gy006-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'10.255.128.0/21;10.255.136.0/21;10.255.144.0/21;10.255.152.0/21;10.255.5.0/24;10.255.7.0/24;10.255.8.0/24;10.255.9.0/24;10.71.241.0/24;172.16.128.0/21;172.18.13.0/24;192.168.4.0/24;192.26.34.0/24;21.15.254.0/24;59.202.240.240/28','zone_no':'cn-hangzhou-zt001-a'},{'disk_type':['dfs1','dfs2'],'route_ip_segment':'10.0.1.0/24;10.0.100.0/24;10.0.110.0/24;10.0.2.0/24;10.0.20.0/24;10.0.30.0/24;10.0.4.0/24;10.0.40.0/24;10.0.5.0/24;10.0.50.0/24;10.0.6.0/24;10.0.60.0/24;10.0.7.0/24;10.0.70.0/24;10.0.8.0/24;10.0.80.0/24;10.0.90.0/24;10.255.5.0/24;10.78.100.0/24;113.215.192.0/24;115.29.12.0/22;115.29.2.0/24;115.29.3.0/24;115.29.8.0/22;172.28.100.0/24;192.168.6.0/24','zone_no':'cn-hangzhou-zt002-a'}]},'msg':'successful'}";
        System.out.println(toJSONString(parseObject(text), true));

        text = "[{'a':1},{'a':2}]";
        System.out.println(JSON.toJSONString(JSON.parse(text), true));
        System.out.println(".......");
        System.out.println();
       

        
    }
}
