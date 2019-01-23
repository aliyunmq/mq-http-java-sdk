package com.aliyun.mq.http.model;

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
     * first consume time
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
        sb.append("receiptHandle='").append(receiptHandle).append('\'');
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
