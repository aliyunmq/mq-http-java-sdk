package com.aliyun.mq.http.common.http;

import com.aliyun.mq.http.common.utils.CaseInsensitiveMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * The base class for message of HTTP request and response.
 */
public abstract class HttpMesssage {

    private Map<String, String> headers = new CaseInsensitiveMap<String>();
    private InputStream content;
    private long contentLength;

    protected HttpMesssage() {
        super();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        assert (headers != null);
        this.headers = headers;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String getHeader(String key) {
        if (this.headers.containsKey(key)) {
            return this.headers.get(key);
        } else {
            return "";
        }
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public void close() throws IOException {
        if (content != null) {
            content.close();
            content = null;
        }
    }
}
