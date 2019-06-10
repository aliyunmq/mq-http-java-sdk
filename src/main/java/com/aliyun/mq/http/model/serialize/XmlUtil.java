package com.aliyun.mq.http.model.serialize;

import com.aliyun.mq.http.common.ClientException;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class XmlUtil {
    private static TransformerFactory transFactory = TransformerFactory.newInstance();
    public static final String PROPERTY_SEPARATOR = "|";
    public static final String PROPERTY_SEPARATOR_SPLIT = "\\|";
    public static final String PROPERTY_KV_SEPARATOR = ":";
    public static final String PROPERTY_KV_SEPARATOR_SPLIT = "\\:";


    public static void output(Node node, String encoding,
                              OutputStream outputStream) throws TransformerException {
        Transformer transformer = transFactory.newTransformer();
        transformer.setOutputProperty("encoding", encoding);

        DOMSource source = new DOMSource();
        source.setNode(node);

        StreamResult result = new StreamResult();
        result.setOutputStream(outputStream);

        transformer.transform(source, result);
    }

    public static String xmlNodeToString(Node node, String encoding)
            throws TransformerException {
        Transformer transformer = transFactory.newTransformer();
        transformer.setOutputProperty("encoding", encoding);
        StringWriter strWtr = new StringWriter();

        DOMSource source = new DOMSource();
        source.setNode(node);
        StreamResult result = new StreamResult(strWtr);
        transformer.transform(source, result);
        return strWtr.toString();

    }

    public static String mapToString(Map<String, String> properties) {
        StringBuilder sb = new StringBuilder();
        if (properties != null) {
            for (final Map.Entry<String, String> entry : properties.entrySet()) {
                final String name = entry.getKey();
                final String value = entry.getValue();

                checkPropValid(name, value);

                sb.append(name);
                sb.append(PROPERTY_KV_SEPARATOR);
                sb.append(value);
                sb.append(PROPERTY_SEPARATOR);
            }
        }
        return sb.toString();
    }

    public static Map<String, String> stringTopMap(String input) {
        Map<String, String> map = new HashMap<String, String>();
        if (input != null) {
            String[] items = input.split(PROPERTY_SEPARATOR_SPLIT);
            for (String i : items) {
                String[] nv = i.split(PROPERTY_KV_SEPARATOR_SPLIT);
                if (2 == nv.length) {
                    map.put(nv[0], nv[1]);
                }
            }
        }
        return map;
    }

    public static void checkPropValid(String key, String value) {
        if (key == null || key.length() <= 0
                || value == null || value.length() <=0) {
            throw new ClientException("Message's property can't be null or empty", "Local");
        }

        if (isContainSpecialChar(key) || isContainSpecialChar(value)) {
            throw new ClientException("Message's property[" + key + ":" + value
                    + "] can't contains: & \" ' < > : |", "LocalClientError");
        }
    }

    public static boolean isContainSpecialChar(String str) {
        return str.contains("&") || str.contains("\"") || str.contains("'")
                || str.contains("<") || str.contains(">")
                || str.contains(XmlUtil.PROPERTY_KV_SEPARATOR)
                || str.contains(XmlUtil.PROPERTY_SEPARATOR);
    }
}
