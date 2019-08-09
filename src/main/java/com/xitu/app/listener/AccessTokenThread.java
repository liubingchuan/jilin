package com.xitu.app.listener;


import java.util.Map;

import org.springframework.util.StringUtils;

import com.xitu.app.constant.Constant;
import com.xitu.app.utils.DateUtil;
import com.xitu.app.utils.GsonUtil;
import com.xitu.app.utils.HttpRequest;
import com.xitu.app.utils.LogUtil;

/**
 * accessToken线程类
 *
 */
public class AccessTokenThread implements Runnable {
    // 微信获取accessToken接口
	private static final String ACCESSTOKENURL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appId}&secret={appSecret}";
    // 当accessToken获取不成功时重试次数
    private static Integer RETRY_TIMES = 3;

    public void run() {
        int retryTimes = 0;
        while (true) {
            String accessToken = getAccessToken();
            if (!StringUtils.isEmpty(accessToken)) {
                Constant.ACCESS_TOKEN = accessToken;
                try {
                    // 90分钟
                    Thread.sleep(90 * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                retryTimes = 0;
            } else {
                if (retryTimes >= RETRY_TIMES) {
                    break;
                }
                retryTimes += 1;
                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取access_Token
     *
     * @return
     */
    public static String getAccessToken() {
        String url = ACCESSTOKENURL.replace("{appId}", Constant.WechatAccount.APPID).replace("{appSecret}", Constant.WechatAccount.APPSECRET);
        String accessTokenJsonStr = HttpRequest.get(url, null, false);
        Map<String, String> accessTokenMap = GsonUtil.fromJson(accessTokenJsonStr, Map.class);
        String accessToken = null;
        if (null != accessTokenMap && !accessTokenMap.isEmpty()) {
            accessToken = accessTokenMap.get("access_token");
            if (!StringUtils.isEmpty(accessToken)) {
                LogUtil.info("时间：" + DateUtil.getCurrentDate(DateUtil.DATETIME_FORMAT) + ";成功获取accessToken；值为：" + accessToken);
            }
        }
        if (StringUtils.isEmpty(accessToken)) {
            String errCode = null;
            String errMsg = null;
            try {
                errCode = accessTokenMap.get("errcode");
                errMsg = accessTokenMap.get("errmsg");
            } catch (Exception e) {
            }
            LogUtil.info("时间：" + DateUtil.getCurrentDate(DateUtil.DATETIME_FORMAT) + ";获取accessToken失败；errCode为：" + errCode + "；errMsg" + errMsg);
        }
        return accessToken;
    }

}