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
        public static final String APPID = "wxbfb9c369fdaa1e25";
        public static final String APPSECRET = "1e14e8d54d9696dcae9a26948077f1da";
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
