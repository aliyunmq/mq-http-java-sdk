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
    protected ServiceClient serviceClient;
    /**
     * topic url, ie: http://uid.mqrest.region.aliyuncs.com/topics/topicName
     */
    protected String topicURL;
    protected String topicName;
    /**
     * object content user auth info
     */
    protected ServiceCredentials credentials;
    /**
     * user mq http endpoint, ie: http://uid.mqrest.region.aliyuncs.com/
     */
    protected URI endpoint;
    /**
     * instance id
     */
    protected String instanceId;

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

    protected void checkMessage(TopicMessage msg) throws ClientException {
        String prop = msg.getProperties().get(Constants.MESSAGE_PROPERTIES_TRANS_CHECK_KEY);
        if (prop == null || prop.length() <= 0) {
            return;
        }
        try {
            Integer.valueOf(prop);
        } catch (Throwable e) {
            throw new ClientException("Should setTransCheckImmunityTime Integer!", "LocalClientError");
        }

        prop = msg.getProperties().get(Constants.MESSAGE_PROPERTIES_TIMER_KEY);
        if (prop == null || prop.length() <= 0) {
            return;
        }
        try {
            Long.valueOf(prop);
        } catch (Throwable e) {
            throw new ClientException("Should setStartDeliverTime Long!", "LocalClientError");
        }
    }

    /**
     * publish message to topic.
     *
     * <pre>
     *     - Timing message : {@link TopicMessage#setStartDeliverTime(long)}
     *     - Ordered message : {@link TopicMessage#setShardingKey(String)}
     * </pre>
     *
     * @param msg message
     * @return message from server
     * @throws ServiceException Exception from server
     * @throws ClientException Exception from client
     */
    public TopicMessage publishMessage(TopicMessage msg) throws ServiceException, ClientException {
        checkMessage(msg);

        PublishMessageRequest request = new PublishMessageRequest();
        request.setMessage(msg);
        request.setInstanceId(instanceId);
        PublishMessageAction action = new PublishMessageAction(serviceClient, credentials, endpoint);
        request.setRequestPath(topicURL + "/" + Constants.LOCATION_MESSAGES);
        return action.executeWithCustomHeaders(request, null);
    }

    /**
     * async publish message to topic.
     *
     * <pre>
     *     - Timing message : {@link TopicMessage#setStartDeliverTime(long)}
     *     - Ordered message : {@link TopicMessage#setShardingKey(String)}, it's recommended to sync publish order message.
     * </pre>
     *
     * @param msg message
     * @param callback, user callback object
     * @return AsyncResult, you can get the result blocked.
     */
    public AsyncResult<TopicMessage> asyncPublishMessage(TopicMessage msg, AsyncCallback<TopicMessage> callback) {
        checkMessage(msg);

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
