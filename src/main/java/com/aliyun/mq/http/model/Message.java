package com.aliyun.mq.http.model;

import com.aliyun.mq.http.common.Constants;

public final class Message extends BaseMessage {
    /**
     * the handle of message, used to ack
     */
    private String receiptHandle;
    /**
     * publish time
     */
    private long publishTime;
    /**
     * if the message is cnosume but not acked, will be consumable at nextConsumeTime
     */
    private long nextConsumeTime;
    /**
     * first consume time, it's meaningless for orderly consume.
     */
    private long firstConsumeTime;
    /**
     * already consumed times
     */
    private Integer consumedTimes;
    private String messageTag;
    private ErrorMessageResult errorMessage;


    public Message() {
        super();
    }

    public Message(byte[] messageBody) {
        super();
        setMessageBody(messageBody);
    }

    public String getReceiptHandle() {
        return receiptHandle;
    }

    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public long getNextConsumeTime() {
        return nextConsumeTime;
    }

    public void setNextConsumeTime(long nextConsumeTime) {
        this.nextConsumeTime = nextConsumeTime;
    }

    public long getFirstConsumeTime() {
        return firstConsumeTime;
    }

    public void setFirstConsumeTime(long firstConsumeTime) {
        this.firstConsumeTime = firstConsumeTime;
    }

    public Integer getConsumedTimes() {
        return consumedTimes;
    }

    public void setConsumedTimes(Integer consumedTimes) {
        this.consumedTimes = consumedTimes;
    }

    public String getMessageTag() {
        return messageTag;
    }

    public void setMessageTag(String messageTag) {
        this.messageTag = messageTag;
    }

    public long getStartDeliverTime() {
        String value = getProperties().get(Constants.MESSAGE_PROPERTIES_TIMER_KEY);
        if (value == null) {
            return 0;
        }
        return Long.valueOf(value);
    }

    public int getTransCheckImmunityTime() {
        String value = getProperties().get(Constants.MESSAGE_PROPERTIES_TRANS_CHECK_KEY);
        if (value == null) {
            return 0;
        }
        return Integer.valueOf(value);
    }

    public String getMessageKey() {
        return getProperties().get(Constants.MESSAGE_PROPERTIES_MSG_KEY);
    }

    public String getShardingKey() {
        return getProperties().get(Constants.MESSAGE_PROPERTIES_SHARDING);
    }

    public ErrorMessageResult getErrorMessageDetail() {
        return this.errorMessage;
    }

    public boolean isErrorMessage() {
        return errorMessage != null;
    }

    public ErrorMessageResult getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessageResult errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append(super.toString());
        sb.append(", receiptHandle='").append(receiptHandle).append('\'');
        sb.append(", publishTime=").append(publishTime);
        sb.append(", nextConsumeTime=").append(nextConsumeTime);
        sb.append(", firstConsumeTime=").append(firstConsumeTime);
        sb.append(", consumedTimes=").append(consumedTimes);
        sb.append(", messageTag='").append(messageTag).append('\'');
        sb.append(", errorMessage=").append(errorMessage);
        sb.append('}');
        return sb.toString();
    }
}
