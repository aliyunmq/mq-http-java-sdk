package com.aliyun.mq.http.model;


import com.aliyun.mq.http.common.Constants;

public class TopicMessage extends BaseMessage {

    private String messageTag;

    /**
     * only transaction msg have;
     */
    private String receiptHandle;

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

    public String getReceiptHandle() {
        return receiptHandle;
    }

    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }

    /**
     * 定时消息，单位毫秒（ms），在指定时间戳（当前时间之后）进行投递。
     * 如果被设置成当前时间戳之前的某个时刻，消息将立刻投递给消费者
     * @param time
     */
    public void setStartDeliverTime(long time) {
        getProperties().put(Constants.MESSAGE_PROPERTIES_TIMER_KEY, String.valueOf(time));
    }

    /**
     * 在消息属性中添加第一次消息回查的最快时间，单位秒，并且表征这是一条事务消息
     * @param seconds
     */
    public void setTransCheckImmunityTime(int seconds) {
        getProperties().put(Constants.MESSAGE_PROPERTIES_TRANS_CHECK_KEY, String.valueOf(seconds));
    }

    /**
     * 设置消息KEY，如果没有设置，则消息的KEY为RequestId
     *
     * @param key 消息KEY
     */
    public void setMessageKey(String key) {
        getProperties().put(Constants.MESSAGE_PROPERTIES_MSG_KEY, key);
    }

    /**
     * 分区顺序消息中区分不同分区的关键字段，sharding key 于普通消息的 key 是完全不同的概念。
     * 全局顺序消息，该字段可以设置为任意非空字符串。
     *
     * @param shardingKey
     */
    public void setShardingKey(String shardingKey) {
        getProperties().put(Constants.MESSAGE_PROPERTIES_SHARDING, shardingKey);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TopicMessage{");
        sb.append(super.toString());
        sb.append("messageTag='").append(messageTag).append('\'');
        sb.append(", receiptHandle='").append(receiptHandle).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
