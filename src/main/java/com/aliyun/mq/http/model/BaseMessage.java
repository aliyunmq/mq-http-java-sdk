package com.aliyun.mq.http.model;

import com.aliyun.mq.http.common.Constants;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseMessage {

    private String requestId;
    private String messageId;
    private String messageBodyMD5;
    private byte[] messageBodyBytes;
    private Map<String, String> properties;


    public BaseMessage() {
        this.requestId = null;
        this.messageId = null;
        this.messageBodyMD5 = null;
        this.messageBodyBytes = null;
        this.properties = new HashMap<String, String>(4);
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * getMessageId
     *
     * @return message id
     */
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * getMessageBodyMD5
     *
     * @return message body md5
     */
    public String getMessageBodyMD5() {
        return messageBodyMD5;
    }

    public void setMessageBodyMD5(String messageBodyMD5) {
        this.messageBodyMD5 = messageBodyMD5;
    }

    /**
     *  set body through byte
     *
     * @param messageBodyBytes byte body
     */
    public void setMessageBody(byte[] messageBodyBytes) {
        this.messageBodyBytes = messageBodyBytes;
    }

    /**
     *  get body as byte
     *
     * @return messageBody
     */
    public byte[] getMessageBodyBytes() {
        return messageBodyBytes;
    }

    /**
     * get body as string, encoded by utf-8
     *
     * @return string body
     */
    public String getMessageBodyString() {
        byte[] messageBodyAsBytes = getMessageBodyBytes();
        if (messageBodyAsBytes == null) {
            return null;
        }
        try {
            return new String(messageBodyAsBytes, Constants.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Not support encoding: " + Constants.DEFAULT_CHARSET);
        }
    }


    /**
     * set body through string, encoded by utf-8
     *
     * @param messageBody string body
     * @throws UnsupportedEncodingException not support encode charset
     */
    public void setMessageBody(String messageBody) throws UnsupportedEncodingException {
        setMessageBody(messageBody.getBytes(Constants.DEFAULT_CHARSET));
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        if (messageId != null) {
            sb.append("MessageID:" + this.messageId + ",");
        }

        if (messageBodyMD5 != null) {
            sb.append("MessageMD5:" + this.messageBodyMD5 + ",");
        }

        if (requestId != null) {
            sb.append("RequestID:" + this.requestId + ",");
        }

        if (properties != null && !properties.isEmpty()) {
            sb.append("Properties:" + this.properties);
        }
        return sb.toString();
    }
}
