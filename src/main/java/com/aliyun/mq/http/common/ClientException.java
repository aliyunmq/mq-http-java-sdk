package com.aliyun.mq.http.common;

public class ClientException extends RuntimeException {

    private static final long serialVersionUID = 1870835486798448798L;

    private String errorCode = ClientErrorCode.UNKNOWN;
    private String requestId;

    public ClientException() {
        super();
    }

    public ClientException(String message, String requestId) {
        super(message);
        this.requestId = requestId;
    }

    public ClientException(Throwable cause) {
        super(cause);
    }

    public ClientException(String message, String requestId, Throwable cause) {
        super(message, cause);
        this.requestId = requestId;
    }

    public ClientException(String errorCode, String message, String requestId, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.requestId = requestId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getRequestId() {
        return requestId;
    }

    @Override
    public String toString() {
        return "[Error Code]:" + errorCode + ", "
                + "[Message]:" + getMessage() + ", "
                + "[RequestId]: " + getRequestId();
    }
}
