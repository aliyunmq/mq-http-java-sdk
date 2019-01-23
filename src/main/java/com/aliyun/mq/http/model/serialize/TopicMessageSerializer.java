package com.aliyun.mq.http.model.serialize;

import com.aliyun.mq.http.common.Constants;
import com.aliyun.mq.http.model.TopicMessage;
import com.aliyun.mq.http.common.utils.BooleanSerializer;
import com.aliyun.mq.http.model.request.PublishMessageRequest;
import com.aliyun.mq.http.model.serialize.XMLSerializer;
import com.aliyun.mq.http.model.serialize.XmlUtil;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TopicMessageSerializer extends XMLSerializer<PublishMessageRequest> {
    private static Gson gson = null;

    private synchronized Gson getGson() {
        if (gson == null) {
            GsonBuilder b = new GsonBuilder();
            b.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
            BooleanSerializer serializer = new BooleanSerializer();
            b.registerTypeAdapter(Boolean.class, serializer);
            b.registerTypeAdapter(boolean.class, serializer);
            gson = b.create();
        }
        return gson;
    }

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

        String xml = XmlUtil.xmlNodeToString(doc, encoding);

        return new ByteArrayInputStream(xml.getBytes(encoding));
    }
}
