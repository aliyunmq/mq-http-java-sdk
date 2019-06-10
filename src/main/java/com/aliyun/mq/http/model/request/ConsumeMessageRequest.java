package com.aliyun.mq.http.model.request;

import com.aliyun.mq.http.model.AbstractRequest;

public class ConsumeMessageRequest extends AbstractRequest {
    private int waitSeconds = 0;
    private int batchSize = 1;
    private String tag;
    private String consumer;
    private String trans = null;

    public int getWaitSeconds() {
        return waitSeconds;
    }

    public void setWaitSeconds(int waitSeconds) {
        this.waitSeconds = waitSeconds;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }
}
