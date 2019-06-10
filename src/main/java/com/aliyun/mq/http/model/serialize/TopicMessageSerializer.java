package com.aliyun.mq.http.model.serialize;

import com.aliyun.mq.http.common.Constants;
import com.aliyun.mq.http.model.TopicMessage;
import com.aliyun.mq.http.model.request.PublishMessageRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TopicMessageSerializer extends XMLSerializer<PublishMessageRequest> {

    @Override
    public InputStream serialize(PublishMessageRequest request, String encoding) throws Exception {
        Document doc = getDocmentBuilder().newDocument();

        TopicMessage msg = request.getMessage();
        Element root = doc.createElementNS(Constants.DEFAULT_XML_NAMESPACE, Constants.MESSAGE_TAG);
        doc.appendChild(root);

        Element node = safeCreateContentElement(doc, Constants.MESSAGE_BODY_TAG,
                msg.getMessageBodyString(), "");
        if (node != null) {
            root.appendChild(node);
        }

        if (msg.getMessageTag() != null && msg.getMessageTag().length() > 0) {
            node = safeCreateContentElement(doc, Constants.MESSAGE_TAG_TAG, msg.getMessageTag(), null);
            if (node != null) {
                root.appendChild(node);
            }
        }

        if (msg.getProperties() != null && !msg.getProperties().isEmpty()) {
            node = safeCreateContentElement(doc, Constants.MESSAGE_PROPERTIES, XmlUtil.mapToString(msg.getProperties()), null);
            if (node != null) {
                root.appendChild(node);
            }
        }

        String xml = XmlUtil.xmlNodeToString(doc, encoding);

        return new ByteArrayInputStream(xml.getBytes(encoding));
    }
}
