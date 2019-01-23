package com.aliyun.mq.http.model;


public class TopicMessage extends BaseMessage {

    private String messageTag;

    public TopicMessage() {
        super();
    }

    public TopicMessage(byte[] body) {
        super();
        setMessageBody(body);
    }

    public TopicMessage(byte[] body, String messageTag) {
        super();
        setMessageBody(body);
        this.messageTag = messageTag;
    }

    public String getMessageTag() {
        return messageTag;
    }

    public void setMessageTag(String messageTag) {
        this.messageTag = messageTag;
    }

}
