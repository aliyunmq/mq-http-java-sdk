package com.aliyun.mq.http.model;

public interface AsyncCallback<T> {
    /**
     * Async callback handler at successfully return.
     *
     * @param result result
     */
    void onSuccess(T result);

    /**
     * Async callback handler at failed return.
     *
     * @param ex error
     */
    void onFail(Exception ex);
}
