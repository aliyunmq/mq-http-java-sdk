package com.aliyun.mq.http.model.serialize;

import com.aliyun.mq.http.common.AckMessageException;
import com.aliyun.mq.http.common.Constants;
import com.aliyun.mq.http.common.ServiceException;
import com.aliyun.mq.http.model.ErrorMessageResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ErrorReceiptHandleListDeserializer extends XMLDeserializer<Exception> {
    @Override
    public Exception deserialize(InputStream stream) throws Exception {

        // byte[] bytes = new byte[1024];
        // while(stream.read(bytes, 0, stream.available())>0){
        // System.out.println(new String(bytes));
        // }
        Document doc = getDocmentBuilder().parse(stream);
        Exception ret = null;
        Element root = doc.getDocumentElement();

        if (root != null) {
            String rootName = root.getNodeName();

            if (rootName == Constants.ERROR_LIST_TAG) {
                NodeList list = doc.getElementsByTagName(Constants.ERROR_TAG);
                if (list != null && list.getLength() > 0) {
                    Map<String, ErrorMessageResult> results = new HashMap<String, ErrorMessageResult>(8);

                    for (int i = 0; i < list.getLength(); i++) {
                        String receiptHandle = parseReceiptHandle((Element) list.item(i));
                        ErrorMessageResult result = parseErrorMessageResult((Element) list.item(i));
                        results.put(receiptHandle, result);

                    }
                    ret = new AckMessageException(results);
                }
            } else if (rootName == Constants.ERROR_TAG) {
                String code = safeGetElementContent(root, Constants.ERROR_CODE_TAG, "");
                String message = safeGetElementContent(root, Constants.ERROR_MESSAGE_TAG, "");
                String requestId = safeGetElementContent(root, Constants.ERROR_REQUEST_ID_TAG, "");
                String hostId = safeGetElementContent(root, Constants.ERROR_HOST_ID_TAG, "");
                ret = new ServiceException(message, null, code, requestId, hostId);
            }
        }
        return ret;
    }

    private String parseReceiptHandle(Element root) {
        return safeGetElementContent(root, Constants.RECEIPT_HANDLE_TAG,
                null);
    }
}
