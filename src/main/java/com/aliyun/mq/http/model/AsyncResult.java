package com.aliyun.mq.http.model;

import org.apache.http.HttpResponse;

import java.util.concurrent.Future;

/**
 * asynchronous call result
 *
 * @param <T> type of Result model
 */
public interface AsyncResult<T> {
    /**
     * get response result
     *
     * @return the result aysnc call return,
     * not null meaning async call successful,
     * wait result until call end.
     */
    T getResult();

    /**
     * get response result blocked timewait
     *
     * @param timewait wait for result in 'timewait' milliseconds.
     * @return as async call result.
     */
    T getResult(long timewait);

    /**
     * check request is success.
     *
     * @return async call is successful(true) or not (false)
     */
    boolean isSuccess();

    /**
     * get error
     *
     * @return async call exception
     */
    Exception getException();

    /**
     * time wait
     *
     * @param timewait wait for result in 'timewait' milliseconds.
     */
    void setTimewait(long timewait);

    /**
     * set future
     *
     * @param future thre http client Future response
     */
    void setFuture(Future<HttpResponse> future);
}
