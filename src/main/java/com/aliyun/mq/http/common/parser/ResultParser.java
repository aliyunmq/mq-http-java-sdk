package com.aliyun.mq.http.common.parser;

import com.aliyun.mq.http.common.http.ResponseMessage;

/**
 * Used to convert an result stream to a java object.
 */
public interface ResultParser<T> {
    /**
     * Converts the result from stream to a java object.
     *
     * @param response The stream of the result.
     * @return The java Type T object that the result stands for.
     * @throws ResultParseException Failed to parse the result.
     */
    T parse(ResponseMessage response) throws ResultParseException;

}