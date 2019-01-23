package com.aliyun.mq.http.model.action;

import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.HttpMethod;
import com.aliyun.mq.http.common.ServiceException;
import com.aliyun.mq.http.common.auth.ServiceCredentials;
import com.aliyun.mq.http.model.AbstractRequest;
import com.aliyun.mq.http.model.AsyncCallback;
import com.aliyun.mq.http.model.AsyncResult;
import com.aliyun.mq.http.common.http.ServiceClient;


public interface Action<T extends AbstractRequest, V> {
    public String getActionName();

    public HttpMethod getMethod();

    public ServiceClient getClient();

    public ServiceCredentials getCredentials();

    public AsyncResult<V> execute(T reqObject, AsyncCallback<V> asyncHandler) throws ClientException, ServiceException;

    public V execute(T reqObject) throws ClientException, ServiceException;
}
