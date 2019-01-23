package com.aliyun.mq.http.common.http;

import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

@NotThreadSafe
public class HttpDeleteWrapper extends HttpEntityEnclosingRequestBase {

    public final static String METHOD_NAME = "DELETE";


    public HttpDeleteWrapper() {
        super();
    }

    public HttpDeleteWrapper(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpDeleteWrapper(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

}