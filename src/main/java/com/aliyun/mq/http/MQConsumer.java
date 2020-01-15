package com.aliyun.mq.http;

import com.aliyun.mq.http.model.AsyncCallback;
import com.aliyun.mq.http.model.AsyncResult;
import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.Constants;
import com.aliyun.mq.http.common.ServiceException;
import com.aliyun.mq.http.common.auth.ServiceCredentials;
import com.aliyun.mq.http.common.http.ServiceClient;
import com.aliyun.mq.http.model.Message;
import com.aliyun.mq.http.model.TopicMessage;
import com.aliyun.mq.http.model.action.AckMessageAction;
import com.aliyun.mq.http.model.action.ConsumeMessageAction;
import com.aliyun.mq.http.model.request.AckMessageRequest;
import com.aliyun.mq.http.model.request.ConsumeMessageRequest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

public class MQConsumer {

    private final ServiceClient serviceClient;
    /**
     * topic url, ie: http://uid.mqrest.region.aliyuncs.com/topics/topicName
     */
    private final String topicURL;
    private final String topicName;
    private final String consumer;
    /**
     * filter messageTag for consumer.
     * If not empty, only consume the message which's @see {@link TopicMessage#messageTag} is equal to it.
     */
    private final String messageTag;
    /**
     * object content user auth info
     */
    private final ServiceCredentials credentials;
    /**
     * user mq http endpoint, ie: http://uid.mqrest.region.aliyuncs.com/
     */
    private final URI endpoint;
    /**
     * instance id
     */
    private String instanceId;

    /**
     * @param instanceId,  instance id
     * @param topicName,   topic name
     * @param consumer     mq cid
     * @param messageTag    message tag for filter
     * @param client,      ServiceClient object
     * @param credentials, ServiceCredentials object
     * @param endpoint,    user mq http endpoint, ie: http://uid.mqrest.region.aliyuncs.com/
     */
    protected MQConsumer(String instanceId, String topicName, String consumer, String messageTag, ServiceClient client,
                         ServiceCredentials credentials, URI endpoint) {
        this.instanceId = instanceId;
        this.serviceClient = client;
        this.credentials = credentials;
        this.endpoint = endpoint;

        String uri = endpoint.toString();
        if (!uri.endsWith(Constants.SLASH)) {
            uri += Constants.SLASH;
        }
        uri += Constants.TPOIC_PREFIX + topicName;
        this.topicURL = uri;
        this.topicName = topicName;
        this.consumer = consumer;
        if (this.consumer == null || this.consumer.isEmpty()) {
            throw new RuntimeException("Consumer can't be empty");
        }
        if (messageTag != null) {
            try {
                this.messageTag = URLEncoder.encode(messageTag, "utf-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("messageTag maybe not utf-8 character.", e);
            }
        } else {
            this.messageTag = null;
        }
    }

    /**
     * the topic name
     *
     * @return topic name
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * the consumer group name of mq
     *
     * @return consumer(client id)
     */
    public String getConsumer() {
        return consumer;
    }

    /**
     * filter message tag, could be null.
     * @return message tag(for filter)
     */
    public String getMessageTag() {
        return messageTag;
    }

    /**
     * instance id
     * @return instance id
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * sync consume message from topic.
     * If the messages gotten by {@link #consumeMessage(int, int)} are not acked by {@link #ackMessage(List)}, they will be consumable again after 300 seconds;
     *
     * @param num           the count of messages consumed once
     *                      value: 1~16
     * @param pollingSecond if greater than 0, means the time(second) the request holden at server if there is no message to consume.
     *                      If less or equal 0, means the server will response back if there is no message to consume.
     *                      value: 1~30
     * @return null or List
     */
    public List<Message> consumeMessage(int num, int pollingSecond)
            throws ServiceException, ClientException {
        ConsumeMessageRequest request = new ConsumeMessageRequest();
        request.setConsumer(this.consumer);
        request.setBatchSize(num);
        request.setTag(messageTag);
        request.setWaitSeconds(pollingSecond);
        request.setInstanceId(this.instanceId);

        try {
            ConsumeMessageAction action = new ConsumeMessageAction(serviceClient, credentials, endpoint);
            request.setRequestPath(topicURL + "/" + Constants.LOCATION_MESSAGES);
            return action.executeWithCustomHeaders(request, null);
        } catch (ServiceException e) {
            if (Constants.CODE_MESSAGE_NOT_EXIST.equals(e.getErrorCode())) {
                return null;
            } else {
                throw e;
            }
        }
    }

    /**
     * sync consume message from topic orderly.
     *
     * Next messages will be consumed if all of same shard are acked. Otherwise, same messages will be consumed again after NextConsumeTime.
     *
     * Attention: the topic should be order topic created at console, if not, mq could not keep the order feature.
     *
     * This interface is suitable for globally order and partitionally order messages, and could be used in multi-thread scenes.
     *
     * @param num           the count of messages consumed once
     *                      value: 1~16
     * @param pollingSecond if greater than 0, means the time(second) the request holden at server if there is no message to consume.
     *                      If less or equal 0, means the server will response back if there is no message to consume.
     *                      value: 1~30
     * @return null or List may contains several shard's messages, the messages of one shard are ordered.
     *                      Get the sharding key through {@link Message#getShardingKey()}
     */
    public List<Message> consumeMessageOrderly(int num, int pollingSecond)
            throws ServiceException, ClientException {
        ConsumeMessageRequest request = new ConsumeMessageRequest();
        request.setConsumer(this.consumer);
        request.setBatchSize(num);
        request.setTag(messageTag);
        request.setWaitSeconds(pollingSecond);
        request.setInstanceId(this.instanceId);
        request.setTrans("order");

        try {
            ConsumeMessageAction action = new ConsumeMessageAction(serviceClient, credentials, endpoint);
            request.setRequestPath(topicURL + "/" + Constants.LOCATION_MESSAGES);
            return action.executeWithCustomHeaders(request, null);
        } catch (ServiceException e) {
            if (Constants.CODE_MESSAGE_NOT_EXIST.equals(e.getErrorCode())) {
                return null;
            } else {
                throw e;
            }
        }
    }

    /**
     * async consume message from topic.
     * If the messages gotten by {@link #consumeMessage(int, int)} are not acked by {@link #ackMessage(List)}, they will be consumable again after 300 seconds;
     *
     * @param num           the count of messages consumed once
     *                      value: 1~16
     * @param pollingSecond if greater than 0, means the time(second) the request holden at server if there is no message to consume.
     *                      If less or equal 0, means the server will response back if there is no message to consume.
     *                      value: 1~30
     * @param callback,     user callback object
     * @return AsyncResult, you can get the result blocked.
     */
    public AsyncResult<List<Message>> asyncConsumeMessage(int num, int pollingSecond, AsyncCallback<List<Message>> callback) {
        ConsumeMessageRequest request = new ConsumeMessageRequest();
        request.setConsumer(consumer);
        request.setBatchSize(num);
        request.setTag(messageTag);
        request.setWaitSeconds(pollingSecond);
        request.setInstanceId(this.instanceId);

        ConsumeMessageAction action = new ConsumeMessageAction(serviceClient, credentials, endpoint);
        request.setRequestPath(topicURL + "/" + Constants.LOCATION_MESSAGES);
        return action.executeWithCustomHeaders(request, callback, null);
    }

    /**
     * Tell server the messages are consumed success.
     * If the messages gotten by {@link #consumeMessage(int, int)} are not acked by {@link #ackMessage(List)}, they will be consumable again after 300 seconds;
     *
     * @param receiptHandles size should be 1~16, @see {@link Message#receiptHandle}
     * @throws ServiceException Exception from server
     * @throws ClientException Exception from client
     */
    public void ackMessage(List<String> receiptHandles) throws ServiceException, ClientException {
        AckMessageAction action = new AckMessageAction(serviceClient, credentials, endpoint);

        AckMessageRequest request = new AckMessageRequest();
        request.setRequestPath(topicURL + "/" + Constants.LOCATION_MESSAGES);
        request.setConsumer(consumer);
        request.setReceiptHandles(receiptHandles);
        request.setInstanceId(this.instanceId);
        action.executeWithCustomHeaders(request, null);
    }

    /**
     * Async tell server the messages are consumed success.
     * If the messages gotten by {@link #consumeMessage(int, int)} are not acked by {@link #ackMessage(List)}, they will be consumable again after 300 seconds;
     *
     * @param receiptHandles size should be 1~16, @see {@link Message#receiptHandle},
     * @param callback,      user callback object
     * @throws ServiceException Exception from server
     * @throws ClientException Exception from client
     *
     * @return AsyncResult, you can get the result blocked.
     */
    public AsyncResult<Void> asyncAckMessage(List<String> receiptHandles, AsyncCallback<Void> callback)
            throws ServiceException, ClientException {
        AckMessageAction action = new AckMessageAction(serviceClient, credentials, endpoint);

        AckMessageRequest request = new AckMessageRequest();
        request.setRequestPath(topicURL + "/" + Constants.LOCATION_MESSAGES);
        request.setConsumer(consumer);
        request.setReceiptHandles(receiptHandles);
        request.setInstanceId(this.instanceId);
        return action.executeWithCustomHeaders(request, callback, null);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MQConsumer{");
        sb.append("topicName='").append(topicName).append('\'');
        sb.append(", consumer='").append(consumer).append('\'');
        sb.append(", messageTag='").append(messageTag).append('\'');
        sb.append(", endpoint=").append(endpoint);
        sb.append(", instanceId='").append(instanceId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
