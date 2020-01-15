package com.aliyun.mq.http.common;

import com.aliyun.mq.http.common.utils.HttpHeaders;

public interface Constants extends HttpHeaders {
    String LOCATION_MESSAGES = "messages";

    String X_HEADER_API_VERSION = "x-mq-version";
    String X_HEADER_API_VERSION_VALUE = "2015-06-06";

    String X_HEADER_PREFIX = "x-mq-";
    String X_HEADER_REQUEST_ID = "x-mq-request-id";


    String DEFAULT_CHARSET = "UTF-8";
    String DEFAULT_CONTENT_TYPE = "text/xml;charset=UTF-8";

    String DEFAULT_XML_NAMESPACE = "http://mq.aliyuncs.com/doc/v1";

    String TPOIC_PREFIX = "topics/";

    String MESSAGE_TAG = "Message";
    String MESSAGE_ID_TAG = "MessageId";

    String RECEIPT_HANDLE_LIST_TAG = "ReceiptHandles";
    String RECEIPT_HANDLE_TAG = "ReceiptHandle";
    String MESSAGE_BODY_TAG = "MessageBody";
    String MESSAGE_BODY_MD5_TAG = "MessageBodyMD5";
    String PUBLISH_TIME_TAG = "PublishTime";
    String NEXT_CONSUME_TIME_TAG = "NextConsumeTime";
    String FIRST_CONSUME_TIME_TAG = "FirstConsumeTime";
    String CONSUMED_TIMES_TAG = "ConsumedTimes";
    String MESSAGE_TAG_TAG = "MessageTag";
    String MESSAGE_PROPERTIES = "Properties";
    String MESSAGE_PROPERTIES_TIMER_KEY = "__STARTDELIVERTIME";
    String MESSAGE_PROPERTIES_TRANS_CHECK_KEY = "__TransCheckT";
    String MESSAGE_PROPERTIES_MSG_KEY = "KEYS";
    String MESSAGE_PROPERTIES_SHARDING = "__SHARDINGKEY";

    String ERROR_LIST_TAG = "Errors";
    String ERROR_TAG = "Error";
    String ERROR_CODE_TAG = "Code";
    String ERROR_MESSAGE_TAG = "Message";
    String ERROR_REQUEST_ID_TAG = "RequestId";
    String ERROR_HOST_ID_TAG = "HostId";
    String MESSAGE_ERRORCODE_TAG = "ErrorCode";
    String MESSAGE_ERRORMESSAGE_TAG = "ErrorMessage";

    String PARAM_WAIT_SECONDS = "waitseconds";
    String PARAM_CONSUMER_TAG = "tag";
    String PARAM_CONSUMER = "consumer";
    String PARAM_CONSUME_NUM = "numOfMessages";
    String PARAM_NS = "ns";
    String PARAM_TRANSACTION = "trans";
    String PARAM_TRANSACTION_V_POP = "pop";
    String PARAM_ORDER = "order";

    String SLASH = "/";
    String HTTP_PREFIX = "http://";
    String HTTPS_PREFIX = "https://";

    String CODE_MESSAGE_NOT_EXIST = "MessageNotExist";
}
