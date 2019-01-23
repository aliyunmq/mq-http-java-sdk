package com.aliyun.mq.http.common.comm;

import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.ServiceException;
import com.aliyun.mq.http.common.http.ServiceClient;

public interface RequestHandler {

    /**
     * pre handle the request
     *
     * @param message request
     * @throws ServiceException Exception from server
     * @throws ClientException Exception from client
     */
    void handle(ServiceClient.Request message) throws ServiceException, ClientException;
}
