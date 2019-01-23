package com.aliyun.mq.http.common.comm;

import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.ServiceException;
import com.aliyun.mq.http.common.http.ResponseMessage;

public interface ResponseHandler {

    /**
     * handle response
     *
     * @param responseData data of response
     * @throws ServiceException Exception from server
     * @throws ClientException Exception from client
     */
    void handle(ResponseMessage responseData)
            throws ServiceException, ClientException;
}
