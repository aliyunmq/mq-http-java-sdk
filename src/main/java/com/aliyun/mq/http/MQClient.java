package com.aliyun.mq.http;

import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.http.ClientConfiguration;
import com.aliyun.mq.http.common.utils.Utils;
import com.aliyun.mq.http.common.auth.ServiceCredentials;
import com.aliyun.mq.http.common.http.ServiceClient;
import com.aliyun.mq.http.common.http.ServiceClientFactory;

import java.net.URI;
import javax.print.DocFlavor;

public class MQClient {

    /**
     * http url
     */
    private URI endpoint;

    /**
     * http client
     */
    private ServiceClient serviceClient;

    /**
     * user info
     */
    private ServiceCredentials credentials;
    private ClientConfiguration config;

    private static volatile MQProducer PRODUCER;
    private static volatile MQTransProducer TRANSACTIONAL_PRODUCER;
    private static volatile MQConsumer CONSUMER;

    private static MQConsumer buildConsumer(String instanceId, String topicName, String consumer, String messageTag, ServiceClient client,
        ServiceCredentials credentials, URI endpoint) {
        return new MQConsumer(instanceId, topicName, consumer, messageTag, client, credentials, endpoint);
    }

    private static MQProducer buildProducer(String instanceId, String topicName, ServiceClient client,
        ServiceCredentials credentials, URI endpoint) {
        return new MQProducer(instanceId, topicName, client, credentials, endpoint);
    }

    private static MQTransProducer buildTransactionalProducer(String instanceId, String topicName, String groupId, ServiceClient client,
        ServiceCredentials credentials, URI endpoint) {
        return new MQTransProducer(instanceId, topicName, groupId, client, credentials, endpoint);
    }

    /**
     * init a MQ client with default client config
     *
     * @param accountEndpoint mq http endpoint, like: http://xxx.mqreset.cn-hangzhou.aliyuncs.com
     * @param accessId        aliyun access id
     * @param accessKey       aliyun access secret key
     */
    public MQClient(String accountEndpoint, String accessId, String accessKey) {
        this(accountEndpoint, accessId, accessKey, null, null);
    }

    /**
     * init a MQ client with defined client config
     *
     * @param accountEndpoint mq http endpoint, like: http://xxx.mqreset.cn-hangzhou.aliyuncs.com
     * @param accessId        aliyun access id
     * @param accessKey       aliyun access secret key
     * @param config          defined client config
     */
    public MQClient(String accountEndpoint, String accessId, String accessKey, ClientConfiguration config) {
        this(accountEndpoint, accessId, accessKey, null, config);
    }

    /**
     * init a MQ client with default client config, use sts token to access mq
     *
     * @param accountEndpoint mq http endpoint, like: http://xxx.mqreset.cn-hangzhou.aliyuncs.com
     * @param accessId        aliyun access id
     * @param accessKey       aliyun access secret key
     * @param securityToken   aliyun sts token
     */
    public MQClient(String accountEndpoint, String accessId, String accessKey, String securityToken) {
        this(accountEndpoint, accessId, accessKey, securityToken, null);
    }

    /**
     * init a MQ client with defined client config, use sts token to access mq
     *
     * @param accountEndpoint mq http endpoint, like: http://xxx.mqreset.cn-hangzhou.aliyuncs.com
     * @param accessId        aliyun access id
     * @param accessKey       aliyun access secret key
     * @param securityToken   aliyun sts token
     * @param config          defined client config
     */
    public MQClient(String accountEndpoint, String accessId, String accessKey, String securityToken, ClientConfiguration config) {
        this.credentials = new ServiceCredentials(accessId, accessKey, securityToken);
        this.endpoint = Utils.getHttpURI(accountEndpoint);
        if (config == null) {
            this.config = new ClientConfiguration();
        } else {
            this.config = config;
        }
        try {
            this.serviceClient = ServiceClientFactory.createServiceClient(this.config);
        } catch (Exception e) {
            if (this.serviceClient != null) {
                ServiceClientFactory.closeServiceClient(serviceClient);
            }
            throw new ClientException(e);
        }
    }

    public void close() {
        synchronized (this) {
            if (isOpen()) {
                ServiceClientFactory.closeServiceClient(this.serviceClient);
            }
        }
    }

    public boolean isOpen() {
        return serviceClient != null && this.serviceClient.isOpen();
    }

    /**
     * default instance
     *
     * @param topicName topic name
     * @return MQProducer
     */
    public MQProducer getProducer(String topicName) {
        if (null == PRODUCER) {
            synchronized (MQClient.class) {
                if (null == PRODUCER) {
                    PRODUCER = buildProducer(null, topicName, this.serviceClient, this.credentials, this.endpoint);
                }
            }
        }
        return PRODUCER;
    }

    /**
     * with instance
     *
     * @param instanceId instance id
     * @param topicName topic name
     * @return MQProducer
     */
    public MQProducer getProducer(String instanceId, String topicName) {
        if (null == PRODUCER) {
            synchronized (MQClient.class) {
                if (null == PRODUCER) {
                    PRODUCER = buildProducer(instanceId, topicName, this.serviceClient, this.credentials, this.endpoint);
                }
            }
        }
        return PRODUCER;
    }

    /**
     * default instance, mq transaction producer
     *
     * @param topicName topic name
     * @param groupId consumer id or group id that is for consume transaction half msg.
     * @return MQTransProducer
     */
    public MQTransProducer getTransProducer(String topicName, String groupId) {
        if (null == TRANSACTIONAL_PRODUCER) {
            synchronized (MQClient.class) {
                if (null == TRANSACTIONAL_PRODUCER) {
                    TRANSACTIONAL_PRODUCER = buildTransactionalProducer(null, topicName, groupId, this.serviceClient, this.credentials, this.endpoint);
                }
            }
        }
        return TRANSACTIONAL_PRODUCER;
    }

    /**
     * with instance, mq transaction producer
     *
     * @param instanceId instance id
     * @param topicName topic name
     * @param groupId consumer id or group id that is for consume transaction half msg.
     * @return MQTransProducer
     */
    public MQTransProducer getTransProducer(String instanceId, String topicName, String groupId) {
        if (null == TRANSACTIONAL_PRODUCER) {
            synchronized (MQClient.class) {
                if (null == TRANSACTIONAL_PRODUCER) {
                    TRANSACTIONAL_PRODUCER = buildTransactionalProducer(instanceId, topicName, groupId, this.serviceClient, this.credentials, this.endpoint);
                }
            }
        }
        return TRANSACTIONAL_PRODUCER;
    }

    /**
     * default instance with filter message tag.
     *
     * @param topicName topic name
     * @param consumer client id
     * @param messageTag message tag
     * @return MQConsumer
     */
    public MQConsumer getConsumer(String topicName, String consumer, String messageTag) {
        if (null == CONSUMER) {
            synchronized (MQClient.class) {
                if (null == CONSUMER) {
                    CONSUMER = buildConsumer(null, topicName, consumer, messageTag, this.serviceClient, this.credentials, this.endpoint);
                }
            }
        }
        return CONSUMER;
    }

    /**
     * default instance
     *
     * @param topicName topic name
     * @param consumer client id
     * @return MQConsumer
     */
    public MQConsumer getConsumer(String topicName, String consumer) {
        if (null == CONSUMER) {
            synchronized (MQClient.class) {
                if (null == CONSUMER) {
                    CONSUMER = buildConsumer(null, topicName, consumer, null, this.serviceClient, this.credentials, this.endpoint);
                }
            }
        }
        return CONSUMER;
    }

    /**
     * instance with filter message tag.
     *
     * @param instanceId instance id
     * @param topicName topic name
     * @param consumer client id
     * @param messageTag message tag
     * @return MQConsumer
     */
    public MQConsumer getConsumer(String instanceId, String topicName, String consumer, String messageTag) {
        if (null == CONSUMER) {
            synchronized (MQClient.class) {
                if (null == CONSUMER) {
                    CONSUMER = buildConsumer(instanceId, topicName, consumer, messageTag, this.serviceClient, this.credentials, this.endpoint);
                }
            }
        }
        return CONSUMER;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MQClient{");
        sb.append("endpoint=").append(endpoint);
        sb.append(", credentials=").append(credentials);
        sb.append('}');
        return sb.toString();
    }
}
