package com.aliyun.mq.http.model.action;

import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.Constants;
import com.aliyun.mq.http.common.HttpMethod;
import com.aliyun.mq.http.common.auth.ServiceCredentials;
import com.aliyun.mq.http.model.TopicMessage;
import com.aliyun.mq.http.common.http.RequestMessage;
import com.aliyun.mq.http.common.http.ResponseMessage;
import com.aliyun.mq.http.common.http.ServiceClient;
import com.aliyun.mq.http.common.parser.ResultParseException;
import com.aliyun.mq.http.common.parser.ResultParser;
import com.aliyun.mq.http.model.request.PublishMessageRequest;
import com.aliyun.mq.http.model.serialize.TopicMessageDeserializer;
import com.aliyun.mq.http.model.serialize.TopicMessageSerializer;

import java.io.InputStream;
import java.net.URI;

public class PublishMessageAction extends AbstractAction<PublishMessageRequest, TopicMessage> {

    public PublishMessageAction(ServiceClient client,
                                ServiceCredentials credentials, URI endpoint) {
        super(HttpMethod.POST, "PublishMessage", client, credentials, endpoint);
    }

    @Override
    protected RequestMessage buildRequest(PublishMessageRequest reqObject)
            throws ClientException {
        RequestMessage requestMessage = new RequestMessage();
        if (reqObject.getInstanceId() != null && reqObject.getInstanceId() != "") {
            requestMessage.setResourcePath(reqObject.getRequestPath() + "?" + Constants.PARAM_NS + "=" + reqObject.getInstanceId());
        } else {
            requestMessage.setResourcePath(reqObject.getRequestPath());
        }
        TopicMessageSerializer serializer = new TopicMessageSerializer();

        try {
            InputStream is = serializer.serialize(reqObject,
                    Constants.DEFAULT_CHARSET);
            requestMessage.setContent(is);
            requestMessage.setContentLength(is.available());
        } catch (Exception e) {
            throw new ClientException(e.getMessage(), null, e);
        }
        return requestMessage;
    }

    @Override
    protected ResultParser<TopicMessage> buildResultParser() {
        return new ResultParser<TopicMessage>() {
            public TopicMessage parse(ResponseMessage response) throws ResultParseException {

                TopicMessageDeserializer deserializer = new TopicMessageDeserializer();
                try {
                    return deserializer.deserialize(response.getContent());
                } catch (Exception e) {
                    logger.warn("Unmarshal error,cause by:" + e.getMessage());
                    throw new ResultParseException(
                            "Unmarshal error,cause by:" + e.getMessage(), e);
                }
            }
        };
    }
}
