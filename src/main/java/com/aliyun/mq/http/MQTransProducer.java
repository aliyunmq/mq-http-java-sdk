package com.aliyun.mq.http;

import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.Constants;
import com.aliyun.mq.http.common.ServiceException;
import com.aliyun.mq.http.common.auth.ServiceCredentials;
import com.aliyun.mq.http.common.http.ServiceClient;
import com.aliyun.mq.http.model.AsyncCallback;
import com.aliyun.mq.http.model.AsyncResult;
import com.aliyun.mq.http.model.Message;
import com.aliyun.mq.http.model.action.AckMessageAction;
import com.aliyun.mq.http.model.action.ConsumeMessageAction;
import com.aliyun.mq.http.model.request.AckMessageRequest;
import com.aliyun.mq.http.model.request.ConsumeMessageRequest;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * transaction producer
 */
public class MQTransProducer extends MQProducer {
    protected final String groupId;

    /**
     * @param instanceId,  instance id
     * @param topicName,   topic name
     * @param client,      ServiceClient object
     * @param credentials, ServiceCredentials object
     * @param endpoint,    user mq http endpoint, ie: http://uid.mqrest.region.aliyuncs.com/
     */
    protected MQTransProducer(String instanceId, String topicName, String groupId, ServiceClient client,
                              ServiceCredentials credentials, URI endpoint) {
        super(instanceId, topicName, client, credentials, endpoint);
        this.groupId = groupId;
    }

    /**
     * consume half message to check transaction status, three choice: {@link #commit(String)} , {@link #rollback(String)}
     * or do nothing (after 10s will get the message again).
     *
     * @param num           the count of messages consumed once
     *                      value: 1~16
     * @param pollingSecond if greater than 0, means the time(second) the request holden at server if there is no message to consume.
     *                      If less or equal 0, means the server will response back if there is no message to consume.
     *                      value: 1~30
     *
     * @throws ServiceException Exception from server
     * @throws ClientException Exception from client
     */
    public List<Message> consumeHalfMessage(int num, int pollingSecond) throws ServiceException, ClientException {
        ConsumeMessageRequest request = new ConsumeMessageRequest();
        request.setBatchSize(num);
        request.setWaitSeconds(pollingSecond);
        request.setInstanceId(this.instanceId);
        request.setConsumer(groupId);
        request.setTrans(Constants.PARAM_TRANSACTION_V_POP);

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
     * consume half message to check transaction status, three choice: {@link #commit(String)} , {@link #rollback(String)}
     * or do nothing (will get the message again after nextConsumeTime).
     *
     * @param num           the count of messages consumed once
     *                      value: 1~16
     * @param pollingSecond if greater than 0, means the time(second) the request holden at server if there is no message to consume.
     *                      If less or equal 0, means the server will response back if there is no message to consume.
     *                      value: 1~30
     * @param callback,     user callback object
     * @return AsyncResult, you can get the result blocked.
     */
    public AsyncResult<List<Message>> asyncConsumeHalfMessage(int num, int pollingSecond, AsyncCallback<List<Message>> callback) {
        ConsumeMessageRequest request = new ConsumeMessageRequest();
        request.setBatchSize(num);
        request.setWaitSeconds(pollingSecond);
        request.setInstanceId(this.instanceId);
        request.setConsumer(groupId);
        request.setTrans(Constants.PARAM_TRANSACTION_V_POP);

        ConsumeMessageAction action = new ConsumeMessageAction(serviceClient, credentials, endpoint);
        request.setRequestPath(topicURL + "/" + Constants.LOCATION_MESSAGES);
        return action.executeWithCustomHeaders(request, callback, null);
    }

    /**
     * commit transaction msg, the consumer will receive the msg.
     *
     * @param handle msg handle
     * @throws ServiceException Exception from server
     * @throws ClientException Exception from client
     */
    public void commit(String handle) throws ServiceException, ClientException {
        AckMessageAction action = new AckMessageAction(serviceClient, credentials, endpoint);
        AckMessageRequest request = new AckMessageRequest();
        request.setRequestPath(topicURL + "/" + Constants.LOCATION_MESSAGES);
        request.setReceiptHandles(Arrays.asList(handle));
        request.setInstanceId(this.instanceId);
        request.setConsumer(groupId);
        request.setTrans("commit");

        action.executeWithCustomHeaders(request, null);
    }

    /**
     * rollback transaction msg, the consumer will not receive the msg.
     *
     * @param handle msg handle
     * @throws ServiceException Exception from server
     * @throws ClientException Exception from client
     */
    public void rollback(String handle) throws ServiceException, ClientException {
        AckMessageAction action = new AckMessageAction(serviceClient, credentials, endpoint);
        AckMessageRequest request = new AckMessageRequest();
        request.setRequestPath(topicURL + "/" + Constants.LOCATION_MESSAGES);
        request.setReceiptHandles(Arrays.asList(handle));
        request.setInstanceId(this.instanceId);
        request.setConsumer(groupId);
        request.setTrans("rollback");

        action.executeWithCustomHeaders(request, null);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MQTransProducer{");
        sb.append("topicName='").append(topicName).append('\'');
        sb.append("groupId='").append(groupId).append('\'');
        sb.append(", endpoint=").append(endpoint);
        sb.append(", instanceId='").append(instanceId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
