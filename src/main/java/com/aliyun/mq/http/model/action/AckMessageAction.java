package com.aliyun.mq.http.model.action;

import com.aliyun.mq.http.common.AckMessageException;
import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.Constants;
import com.aliyun.mq.http.common.HttpMethod;
import com.aliyun.mq.http.common.auth.ServiceCredentials;
import com.aliyun.mq.http.common.http.RequestMessage;
import com.aliyun.mq.http.common.http.ServiceClient;
import com.aliyun.mq.http.common.parser.ResultParseException;
import com.aliyun.mq.http.common.parser.ResultParser;
import com.aliyun.mq.http.model.serialize.ErrorReceiptHandleListDeserializer;
import com.aliyun.mq.http.model.serialize.ReceiptHandleListSerializer;
import com.aliyun.mq.http.common.http.ResponseMessage;
import com.aliyun.mq.http.model.request.AckMessageRequest;

import java.io.InputStream;
import java.net.URI;

public class AckMessageAction extends AbstractAction<AckMessageRequest, Void> {

    public AckMessageAction(ServiceClient client,
                            ServiceCredentials credentials, URI endpoint) {
        super(HttpMethod.DELETE, "AckMessage", client, credentials,
                endpoint);
    }

    @Override
    protected RequestMessage buildRequest(AckMessageRequest reqObject)
            throws ClientException {
        RequestMessage requestMessage = new RequestMessage();

        if (reqObject.getReceiptHandles() == null || reqObject.getReceiptHandles().isEmpty()) {
            throw new ClientException("ReceiptHandles can not be null or empty!", "LocalCheck");
        }

        if (reqObject.getConsumer() == null || reqObject.getConsumer().isEmpty()) {
            throw new ClientException("Consumer can not be empty!", "LocalCheck");
        }

        String uri = reqObject.getRequestPath() + "?" + Constants.PARAM_CONSUMER + "=" + reqObject.getConsumer();
        if (reqObject.getInstanceId() != null && reqObject.getInstanceId() != "") {
            uri += "&" + Constants.PARAM_NS + "=" + reqObject.getInstanceId();
        }
        if (reqObject.getTrans() != null && reqObject.getTrans().length() > 0) {
            uri += "&" + Constants.PARAM_TRANSACTION + "=" + reqObject.getTrans();
        }
        requestMessage.setResourcePath(uri);
        try {
            ReceiptHandleListSerializer serializer = new ReceiptHandleListSerializer();
            InputStream is = serializer.serialize(
                    reqObject.getReceiptHandles(), Constants.DEFAULT_CHARSET);
            requestMessage.setContent(is);
            requestMessage.setContentLength(is.available());
        } catch (Exception e) {
            throw new ClientException(e.getMessage(), null, e);
        }
        return requestMessage;
    }

    @Override
    protected ResultParser<Void> buildResultParser() {
        return null;
    }

    @Override
    protected ResultParser<Exception> buildExceptionParser() {
        return new ResultParser<Exception>() {
            public Exception parse(ResponseMessage response)
                    throws ResultParseException {
                ErrorReceiptHandleListDeserializer deserializer = new ErrorReceiptHandleListDeserializer();
                try {
                    Exception ret = deserializer.deserialize(response.getContent());
                    if (ret instanceof AckMessageException) {
                        ((AckMessageException) ret).setRequestId(response.getHeader(Constants.X_HEADER_REQUEST_ID));
                    }
                    return ret;
                } catch (Exception e) {
                    throw new ResultParseException("Unmarshal error,cause by:" + e.getMessage(), e);
                }
            }
        };
    }
}
