package com.aliyun.mq.http.model.action;

import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.Constants;
import com.aliyun.mq.http.common.HttpMethod;
import com.aliyun.mq.http.common.auth.ServiceCredentials;
import com.aliyun.mq.http.common.http.RequestMessage;
import com.aliyun.mq.http.common.http.ServiceClient;
import com.aliyun.mq.http.common.parser.ResultParseException;
import com.aliyun.mq.http.common.parser.ResultParser;
import com.aliyun.mq.http.model.Message;
import com.aliyun.mq.http.model.request.ConsumeMessageRequest;
import com.aliyun.mq.http.model.serialize.MessageListDeserializer;
import com.aliyun.mq.http.common.http.ResponseMessage;

import java.net.URI;
import java.util.List;

public class ConsumeMessageAction extends
        AbstractAction<ConsumeMessageRequest, List<Message>> {

    public ConsumeMessageAction(ServiceClient client, ServiceCredentials credentials, URI endpoint) {
        super(HttpMethod.GET, "ConsumeMessage", client, credentials, endpoint);
    }

    @Override
    protected RequestMessage buildRequest(ConsumeMessageRequest reqObject)
            throws ClientException {
        RequestMessage requestMessage = new RequestMessage();

        if (reqObject.getBatchSize() < 1) {
            throw new ClientException("Consume num should greater then 0!", "LocalCheck");
        }

        String uri = reqObject.getRequestPath() + "?" + Constants.PARAM_CONSUME_NUM + "=" + reqObject.getBatchSize();

        if (reqObject.getConsumer() != null && reqObject.getConsumer().length() > 0) {
            uri += "&" + Constants.PARAM_CONSUMER + "=" + reqObject.getConsumer();
        } else {
            throw new ClientException("Consumer can not be empty!", "LocalCheck");
        }
        if (reqObject.getInstanceId() != null && reqObject.getInstanceId() != "") {
            uri += "&" + Constants.PARAM_NS + "=" + reqObject.getInstanceId();
        }
        if (reqObject.getTag() != null && reqObject.getTag().length() > 0) {
            uri += "&" + Constants.PARAM_CONSUMER_TAG + "=" + reqObject.getTag();
        }
        if (reqObject.getWaitSeconds() > 0) {
            uri += "&" + Constants.PARAM_WAIT_SECONDS + "=" + reqObject.getWaitSeconds();
        }
        if (reqObject.getTrans() != null && reqObject.getTrans().length() > 0) {
            uri += "&" + Constants.PARAM_TRANSACTION + "=" + reqObject.getTrans();
        }

        requestMessage.setResourcePath(uri);
        return requestMessage;
    }

    @Override
    protected ResultParser<List<Message>> buildResultParser() {
        return new ResultParser<List<Message>>() {
            public List<Message> parse(ResponseMessage response) throws ResultParseException {
                MessageListDeserializer deserializer = new MessageListDeserializer();
                try {
                    List<Message> msgs = deserializer.deserialize(response.getContent());
                    for (Message msg : msgs) {
                        msg.setRequestId(response.getHeader(Constants.X_HEADER_REQUEST_ID));
                    }
                    return msgs;
                } catch (Exception e) {
                    throw new ResultParseException("Unmarshal error,cause by:"
                            + e.getMessage(), e);
                }
            }
        };
    }
}
