package com.aliyun.mq.http.model.serialize;

import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.Constants;
import com.aliyun.mq.http.model.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MessageListDeserializer extends XMLDeserializer<List<Message>> {
    @Override
    public List<Message> deserialize(InputStream stream) throws Exception {

        // byte[] bytes = new byte[1024];
        // while(stream.read(bytes, 0, stream.available())>0){
        // System.out.println(new String(bytes));
        // }
        Document doc = getDocmentBuilder().parse(stream);
        return deserialize(doc);

    }

    public List<Message> deserialize(Document doc) {
        NodeList list = doc.getElementsByTagName(Constants.MESSAGE_TAG);
        if (list != null && list.getLength() > 0) {
            List<Message> results = new ArrayList<Message>();

            for (int i = 0; i < list.getLength(); i++) {
                Message msg = parseMessage((Element) list.item(i));
                results.add(msg);
            }
            return results;
        }
        return null;
    }

    private Message parseMessage(Element root) throws ClientException {
        Message message = new Message();

        String messageId = safeGetElementContent(root, Constants.MESSAGE_ID_TAG, null);
        if (messageId == null) {
            message.setErrorMessage(parseErrorMessageResult(root));
            return message;
        }

        message.setMessageId(messageId);
        String messageBody = safeGetElementContent(root, Constants.MESSAGE_BODY_TAG, null);
        if (messageBody != null) {
            try {
                message.setMessageBody(messageBody);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Not support enconding:" + Constants.DEFAULT_CHARSET);
            }
        }

        String messageBodyMD5 = safeGetElementContent(root,
                Constants.MESSAGE_BODY_MD5_TAG, null);
        message.setMessageBodyMD5(messageBodyMD5);

        String receiptHandle = safeGetElementContent(root, Constants.RECEIPT_HANDLE_TAG,
                null);
        message.setReceiptHandle(receiptHandle);

        String publishTime = safeGetElementContent(root, Constants.PUBLISH_TIME_TAG, null);
        if (publishTime != null) {
            message.setPublishTime(Long.parseLong(publishTime));
        }

        String nextConsumeTime = safeGetElementContent(root, Constants.NEXT_CONSUME_TIME_TAG, null);
        if (nextConsumeTime != null) {
            message.setNextConsumeTime(Long.parseLong(nextConsumeTime));
        }

        String firstConsumeTime = safeGetElementContent(root, Constants.FIRST_CONSUME_TIME_TAG, null);
        if (firstConsumeTime != null) {
            message.setFirstConsumeTime(Long.parseLong(firstConsumeTime));
        }

        String consumedTimes = safeGetElementContent(root, Constants.CONSUMED_TIMES_TAG,
                null);
        if (consumedTimes != null) {
            message.setConsumedTimes(Integer.parseInt(consumedTimes));
        }

        message.setMessageTag(safeGetElementContent(root, Constants.MESSAGE_TAG_TAG, null));

        String properties = safeGetElementContent(root, Constants.MESSAGE_PROPERTIES,
                null);
        if (properties != null) {
            message.setProperties(XmlUtil.stringTopMap(properties));
        }

        return message;
    }
}
