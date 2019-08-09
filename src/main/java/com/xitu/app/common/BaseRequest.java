package com.xitu.app.common;

import java.io.Serializable;

public class BaseRequest implements Serializable {

    public static final long serialVersionUID = 1L;

    /**
     * 可选， 访问API请求的action,
     */
    protected String         action;

    /**
     * 必选， 请求ID，如果请求者未输入requestId，系统会自动生成uuid做为requestId
     */
    protected String         requestId;

    /**
     * 可选，调用者的ip
     */
    protected String         remoteIp;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

}
