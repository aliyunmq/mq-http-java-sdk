package com.aliyun.mq.http.model.serialize;

import com.aliyun.mq.http.common.Constants;
import com.aliyun.mq.http.model.serialize.XMLSerializer;
import com.aliyun.mq.http.model.serialize.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class ReceiptHandleListSerializer extends XMLSerializer<List<String>> {

    @Override
    public InputStream serialize(List<String> receipts, String encoding) throws Exception {
        Document doc = getDocmentBuilder().newDocument();

        Element root = doc.createElementNS(Constants.DEFAULT_XML_NAMESPACE, Constants.RECEIPT_HANDLE_LIST_TAG);

        doc.appendChild(root);

        for (String receipt : receipts) {
            Element node = safeCreateContentElement(doc, Constants.RECEIPT_HANDLE_TAG,
                    receipt, "");

            if (node != null) {
                root.appendChild(node);
            }
        }
        String xml = XmlUtil.xmlNodeToString(doc, encoding);

        return new ByteArrayInputStream(xml.getBytes(encoding));
    }
}
