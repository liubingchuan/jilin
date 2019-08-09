package com.xitu.app.service;


import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.xitu.app.constant.Constant;
import com.xitu.app.model.TextReplyMsg;
import com.xitu.app.utils.GsonUtil;
import com.xitu.app.utils.HttpRequest;
import com.xitu.app.utils.XmlUtil;

/**
 * 回复消息
 */
@Service
public class MsgServiceImpl implements IMsgService {
    // 机器人接口
    private static final String AITEXTREPLYURL = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=";

    @Override
    public String returnText(Map<String, String> map) {
        // 构建回复消息
        TextReplyMsg textReplyMsg = new TextReplyMsg();
        textReplyMsg.setToUserName(map.get("FromUserName"));
        textReplyMsg.setFromUserName(map.get("ToUserName"));
        textReplyMsg.setCreateTime(new Date().getTime());
        textReplyMsg.setMsgType(Constant.MsgType.TEXT);

        String url = AITEXTREPLYURL + map.get("Content");
        // {"result":0,"content":"*^_^*好好好~"}
        String aiMessageStr = HttpRequest.get(url, null, false);
        Map<String, Object> aiMap = GsonUtil.fromJson(aiMessageStr, Map.class);
        textReplyMsg.setContent(aiMap.get("content").toString());
        // 将pojo对象转成xml
        XmlUtil.xstream.alias("xml", TextReplyMsg.class);
        return XmlUtil.xstream.toXML(textReplyMsg);
    }

    @Override
    public String returnText(Map<String, String> map, String content) {
        // 构建回复消息
        TextReplyMsg textReplyMsg = new TextReplyMsg();
        textReplyMsg.setToUserName(map.get("FromUserName"));
        textReplyMsg.setFromUserName(map.get("ToUserName"));
        textReplyMsg.setCreateTime(new Date().getTime());
        textReplyMsg.setMsgType(Constant.MsgType.TEXT);
        textReplyMsg.setContent(content);
        // 将pojo对象转成xml
        XmlUtil.xstream.alias("xml", TextReplyMsg.class);
        return XmlUtil.xstream.toXML(textReplyMsg);
    }
}
