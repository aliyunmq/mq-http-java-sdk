package com.aliyun.mq.http.model.request;

import com.aliyun.mq.http.model.AbstractRequest;

import java.util.List;

public class AckMessageRequest extends AbstractRequest {
    private String consumer;
    private List<String> receiptHandles;
    private String trans;

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public List<String> getReceiptHandles() {
        return receiptHandles;
    }

    public void setReceiptHandles(List<String> receiptHandles) {
        this.receiptHandles = receiptHandles;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }
}
