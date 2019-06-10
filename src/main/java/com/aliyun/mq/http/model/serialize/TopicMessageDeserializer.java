package com.aliyun.mq.http.model.serialize;


import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.Constants;
import com.aliyun.mq.http.model.TopicMessage;
import com.aliyun.mq.http.model.serialize.XMLDeserializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;

public class TopicMessageDeserializer extends XMLDeserializer<TopicMessage> {
    public TopicMessageDeserializer() {
    }

    public TopicMessage deserialize(InputStream stream) throws Exception {
        Document doc = getDocmentBuilder().parse(stream);

        Element root = doc.getDocumentElement();
        return parseMessage(root);


    }

    private TopicMessage parseMessage(Element root) throws ClientException {
        TopicMessage message = new TopicMessage();

        String messageId = safeGetElementContent(root, Constants.MESSAGE_ID_TAG, null);
        message.setMessageId(messageId);

        String messageBodyMD5 = safeGetElementContent(root,
                Constants.MESSAGE_BODY_MD5_TAG, null);
        message.setMessageBodyMD5(messageBodyMD5);

        String handle = safeGetElementContent(root, Constants.RECEIPT_HANDLE_TAG, null);
        message.setReceiptHandle(handle);

        return message;
    }
}
