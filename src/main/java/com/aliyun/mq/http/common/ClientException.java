package com.aliyun.mq.http.common;

public class ClientException extends RuntimeException {

    private static final long serialVersionUID = 1870835486798448798L;

    private String errorCode = ClientErrorCode.UNKNOWN;
    private String requestId;

    /**
     * 构造新实例。
     */
    public ClientException() {
        super();
    }

    /**
     * 用给定的异常信息构造新实例。
     *
     * @param message 异常信息。
     */
    public ClientException(String message, String requestId) {
        super(message);
        this.requestId = requestId;
    }

    /**
     * 用表示异常原因的对象构造新实例。
     *
     * @param cause 异常原因。
     */
    public ClientException(Throwable cause) {
        super(cause);
    }

    /**
     * 用异常消息和表示异常原因的对象构造新实例。
     *
     * @param message 异常信息。
     * @param cause   异常原因。
     */
    public ClientException(String message, String requestId, Throwable cause) {
        super(message, cause);
        this.requestId = requestId;
    }

    /**
     * 用异常消息和表示异常原因的对象构造新实例。
     *
     * @param errorCode 错误码
     * @param message   异常信息。
     * @param cause     异常原因。
     */
    public ClientException(String errorCode, String message, String requestId, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.requestId = requestId;
    }

    /**
     * 获取异常的错误码
     *
     * @return 异常错误码
     */
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
