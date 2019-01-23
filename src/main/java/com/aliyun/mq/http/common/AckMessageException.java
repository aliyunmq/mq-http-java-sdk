package com.aliyun.mq.http.common;

import com.aliyun.mq.http.model.ErrorMessageResult;

import java.util.Map;

public class AckMessageException extends ServiceException {
    /**
     *
     */
    private static final long serialVersionUID = -7705861423905005565L;
    private Map<String, ErrorMessageResult> errorMessages;

    public AckMessageException(Map<String, ErrorMessageResult> errorMsgs) {
        this.errorMessages = errorMsgs;
    }

    public Map<String, ErrorMessageResult> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(Map<String, ErrorMessageResult> errorMessages) {
        this.errorMessages = errorMessages;
    }
}
