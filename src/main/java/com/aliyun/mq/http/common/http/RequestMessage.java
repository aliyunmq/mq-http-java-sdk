package com.aliyun.mq.http.common.http;

import com.aliyun.mq.http.common.HttpMethod;
import com.aliyun.mq.http.common.utils.CodingUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class RequestMessage extends HttpMesssage {
    private HttpMethod method = HttpMethod.GET; // HTTP Method. default GET.
    private URI endpoint;
    private String resourcePath;
    private Map<String, String> parameters = new HashMap<String, String>();

    public RequestMessage() {
    }

    /**
     * 获取HTTP的请求方法。
     *
     * @return HTTP的请求方法。
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * 设置HTTP的请求方法。
     *
     * @param method HTTP的请求方法。
     */
    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    /**
     * @return the endpoint
     */
    public URI getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint(URI endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * @return the resourcePath
     */
    public String getResourcePath() {
        return resourcePath;
    }

    /**
     * @param resourcePath the resourcePath to set
     */
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    /**
     * @return the parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(Map<String, String> parameters) {
        CodingUtils.assertParameterNotNull(parameters, "parameters");

        this.parameters = parameters;
    }

    public void addParameter(String key, String value) {
        CodingUtils.assertStringNotNullOrEmpty(key, "key");

        this.parameters.put(key, value);
    }

    public void removeParameter(String key) {
        CodingUtils.assertStringNotNullOrEmpty(key, "key");

        this.parameters.remove(key);
    }

    /**
     * Whether or not the request can be repeatedly sent.
     *
     * @return isRepeatable
     */
    public boolean isRepeatable() {
        return this.getContent() == null || this.getContent().markSupported();
    }
}