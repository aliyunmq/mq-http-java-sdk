package com.aliyun.mq.http;

import com.aliyun.mq.http.model.AsyncCallback;
import com.aliyun.mq.http.model.AsyncResult;
import com.aliyun.mq.http.model.action.PublishMessageAction;
import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.Constants;
import com.aliyun.mq.http.common.ServiceException;
import com.aliyun.mq.http.common.auth.ServiceCredentials;
import com.aliyun.mq.http.common.http.ServiceClient;
import com.aliyun.mq.http.model.TopicMessage;
import com.aliyun.mq.http.model.request.PublishMessageRequest;

import java.net.URI;

public class MQProducer {
    private ServiceClient serviceClient;
    /**
     * topic url, ie: http://uid.mqrest.region.aliyuncs.com/topics/topicName
     */
    private String topicURL;
    private String topicName;
    /**
     * object content user auth info
     */
    private ServiceCredentials credentials;
    /**
     * user mq http endpoint, ie: http://uid.mqrest.region.aliyuncs.com/
     */
    private URI endpoint;
    /**
     * instance id
     */
    private String instanceId;

    /**
     * @param instanceId,  instance id
     * @param topicName,   topic name
     * @param client,      ServiceClient object
     * @param credentials, ServiceCredentials object
     * @param endpoint,    user mq http endpoint, ie: http://uid.mqrest.region.aliyuncs.com/
     */
    protected MQProducer(String instanceId, String topicName, ServiceClient client,
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
    }

    public String getTopicName() {
        return topicName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    /**
     * publish message to topic
     *
     * @param msg message
     * @return message from server
     * @throws ServiceException Exception from server
     * @throws ClientException Exception from client
     */
    public TopicMessage publishMessage(TopicMessage msg) throws ServiceException, ClientException {
        PublishMessageRequest request = new PublishMessageRequest();
        request.setMessage(msg);
        request.setInstanceId(instanceId);
        PublishMessageAction action = new PublishMessageAction(serviceClient, credentials, endpoint);
        request.setRequestPath(topicURL + "/" + Constants.LOCATION_MESSAGES);
        return action.executeWithCustomHeaders(request, null);
    }

    /**
     * async publish message to topic
     * so, when you receive this message, you should do base64 decode before use it.
     *
     * @param msg message
     * @param callback, user callback object
     * @return AsyncResult, you can get the result blocked.
     */
    public AsyncResult<TopicMessage> asyncPublishMessage(TopicMessage msg, AsyncCallback<TopicMessage> callback) {
        PublishMessageRequest request = new PublishMessageRequest();
        request.setMessage(msg);
        request.setInstanceId(instanceId);
        PublishMessageAction action = new PublishMessageAction(serviceClient, credentials, endpoint);
        request.setRequestPath(topicURL + "/" + Constants.LOCATION_MESSAGES);
        return action.executeWithCustomHeaders(request, callback, null);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MQProducer{");
        sb.append("topicName='").append(topicName).append('\'');
        sb.append(", endpoint=").append(endpoint);
        sb.append(", instanceId='").append(instanceId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
