package com.aliyun.mq.http.common.comm;

import com.aliyun.mq.http.common.http.RequestMessage;
import com.aliyun.mq.http.common.http.ResponseMessage;

public class NoRetryStrategy extends RetryStrategy {

    @Override
    public boolean shouldRetry(Exception ex, RequestMessage request,
                               ResponseMessage response, int retries) {
        return false;
    }

}
