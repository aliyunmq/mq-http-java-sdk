package com.aliyun.mq.http.model.serialize;

import com.aliyun.mq.http.common.Constants;
import com.aliyun.mq.http.model.ErrorMessageResult;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class XMLDeserializer<T> extends BaseXMLSerializer<T> implements Deserializer<T> {

    public String safeGetElementContent(Element root, String tagName,
                                        String defualValue) {
        NodeList nodes = root.getElementsByTagName(tagName);
        if (nodes != null) {
            Node node = nodes.item(0);
            if (node == null) {
                return defualValue;
            } else {
                return node.getTextContent();
            }
        }
        return defualValue;
    }

    protected ErrorMessageResult parseErrorMessageResult(Element root) {
        ErrorMessageResult result = new ErrorMessageResult();
        String errorCode = safeGetElementContent(root, Constants.MESSAGE_ERRORCODE_TAG,
                null);
        result.setErrorCode(errorCode);

        String errorMessage = safeGetElementContent(root,
                Constants.MESSAGE_ERRORMESSAGE_TAG, null);
        result.setErrorMessage(errorMessage);
        return result;
    }

}
