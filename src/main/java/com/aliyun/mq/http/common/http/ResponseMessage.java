package com.aliyun.mq.http.common.http;

/**
 * 表示返回结果的信息。
 */
public class ResponseMessage extends HttpMesssage {
    private static final int HTTP_SUCCESS_STATUS_CODE = 200;
    private String uri;
    private int statusCode;

    /**
     * 构造函数。
     */
    public ResponseMessage() {
    }

    public String getUri() {
        return uri;
    }

    public void setUrl(String uri) {
        this.uri = uri;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSuccessful() {
        return statusCode / 100 == HTTP_SUCCESS_STATUS_CODE / 100;
    }
}
