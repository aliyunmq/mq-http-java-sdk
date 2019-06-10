package com.aliyun.mq.http.model.serialize;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class XMLSerializer<T> extends BaseXMLSerializer<T> implements Serializer<T> {

    public static Element safeCreateContentElement(Document doc, String tagName,
                                            Object value, String defaultValue) {
        if (value == null && defaultValue == null) {
            return null;
        }

        Element node = doc.createElement(tagName);
        if (value != null) {
            node.setTextContent(value.toString());
        } else {
            node.setTextContent(defaultValue);
        }
        return node;
    }

}
