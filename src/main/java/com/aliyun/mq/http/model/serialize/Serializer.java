package com.aliyun.mq.http.model.serialize;

import java.io.InputStream;

public interface Serializer<T> {
    InputStream serialize(T obj, String encoding) throws Exception;
}
