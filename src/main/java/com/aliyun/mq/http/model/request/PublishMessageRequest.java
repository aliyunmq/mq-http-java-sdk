package com.aliyun.mq.http.model.request;

import com.aliyun.mq.http.model.AbstractRequest;
import com.aliyun.mq.http.model.TopicMessage;


public class PublishMessageRequest extends AbstractRequest {
    private TopicMessage message;

    public TopicMessage getMessage() {
        return message;
    }

    public void setMessage(TopicMessage message) {
        this.message = message;
    }
}
