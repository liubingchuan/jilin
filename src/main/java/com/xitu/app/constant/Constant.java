package com.xitu.app.constant;

/**
 * 常量类
 */
public class Constant {
    /**
     * accessToken
     */
    public static String ACCESS_TOKEN = "";

    /**
     * 微信公众号参数
     */
    public class WechatAccount {
        public static final String APPID = "wxbfddb4887bbd4b14";
        public static final String APPSECRET = "893688bfd55e542cb7ca2428b5d96a06";
        public static final String TOKEN = "lexuan";
        public static final String ENCODINGAESKEY = "UZuANofjLHCcoXe765Or1xoJKABN4IVvWXxdtM4yvPK";
    }

    /**
     * 消息类型
     */
    public class MsgType {
        public static final String TEXT = "text";
        public static final String EVENT = "event";
    }

    /**
     * 事件类型
     */
    public class Event {
        // 订阅
        public static final String SUBSCRIBE = "subscribe";
        public static final String CLICK = "CLICK";
        public static final String SCAN = "SCAN";
    }

    /**
     * 请求头类型
     */
    public class ContentType{
        public static final String APPLICATION_JSON = "application/json;charset=utf-8";
    }
}
