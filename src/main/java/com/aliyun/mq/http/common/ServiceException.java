package com.aliyun.mq.http.common;

/**
 * Service Exception
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 430933593095358673L;
    private String errorCode;
    private String requestId;
    private String hostId;

    public ServiceException() {
        super();
        this.errorCode = "";
    }

    public ServiceException(String message, String requestId) {
        super(message);
        this.requestId = requestId;
        this.errorCode = "";
    }

    public ServiceException(Throwable cause) {
        super(cause);
        this.errorCode = "";
    }

    public ServiceException(String message, String requestId, Throwable cause) {
        super(message, cause);
        this.requestId = requestId;
        this.errorCode = "";
    }

    /**
     * 用异常消息和表示异常原因及其他信息的对象构造新实例。
     *
     * @param message   异常信息。
     * @param cause     异常原因。
     * @param errorCode 错误代码。
     * @param requestId Request ID。
     * @param hostId    Host ID。
     */
    public ServiceException(String message, Throwable cause,
                            String errorCode, String requestId, String hostId) {
        this(message, requestId, cause);

        if (errorCode != null) {
            this.errorCode = errorCode;
        }
        this.hostId = hostId;
    }

    /**
     * 返回错误代码的字符串表示。
     *
     * @return 错误代码的字符串表示。
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 返回Request标识。
     *
     * @return Request标识。
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * 返回Host标识。
     *
     * @return Host标识。
     */
    public String getHostId() {
        return hostId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "[Error Code]:" + errorCode + ", "
                + "[Message]:" + getMessage() + ", "
                + "[RequestId]: " + getRequestId();
    }
}