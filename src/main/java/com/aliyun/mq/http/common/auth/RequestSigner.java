package com.aliyun.mq.http.common.auth;

import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.http.RequestMessage;

public interface RequestSigner {

    public void sign(RequestMessage request)
            throws ClientException;
}
