package com.aliyun.mq.http.model.action;

import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.Constants;
import com.aliyun.mq.http.common.HttpMethod;
import com.aliyun.mq.http.common.ServiceException;
import com.aliyun.mq.http.common.auth.ServiceCredentials;
import com.aliyun.mq.http.common.auth.ServiceSignature;
import com.aliyun.mq.http.common.utils.Utils;
import com.aliyun.mq.http.model.AbstractRequest;
import com.aliyun.mq.http.model.AsyncCallback;
import com.aliyun.mq.http.model.AsyncResult;
import com.aliyun.mq.http.common.comm.ExecutionContext;
import com.aliyun.mq.http.common.http.ExceptionResultParser;
import com.aliyun.mq.http.common.http.HttpCallback;
import com.aliyun.mq.http.common.http.RequestMessage;
import com.aliyun.mq.http.common.http.ServiceClient;
import com.aliyun.mq.http.common.parser.ResultParser;
import com.aliyun.mq.http.common.utils.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Future;

public abstract class AbstractAction<T extends AbstractRequest, V> implements
        Action<T, V> {

    public static Log logger = LogFactory.getLog(Utils.class);
    protected String actionName = "";
    private ServiceClient client;
    private ServiceCredentials credentials;
    private HttpMethod method;
    private URI endpoint;

    public AbstractAction(HttpMethod method, String actionName,
                          ServiceClient client, ServiceCredentials credentials, URI endpoint) {
        this.method = method;
        this.actionName = actionName;
        this.client = client;
        this.endpoint = endpoint;
        this.credentials = credentials;
    }

    private static TreeMap<String, String> sortHeader(
            Map<String, String> headers) {
        TreeMap<String, String> tmpHeaders = new TreeMap<String, String>();
        Set<String> keySet = headers.keySet();
        for (String key : keySet) {
            if (key.toLowerCase().startsWith(Constants.X_HEADER_PREFIX)) {
                tmpHeaders.put(key.toLowerCase(), headers.get(key));
            } else {
                tmpHeaders.put(key, headers.get(key));
            }
        }
        return tmpHeaders;
    }

    private static String safeGetHeader(String key, Map<String, String> headers) {
        if (headers == null) {
            return "";
        }
        String value = headers.get(key);
        if (value == null) {
            return "";
        }
        return value;
    }

    public String getActionName() {
        return actionName;
    }

    public ServiceClient getClient() {
        return client;
    }

    public ServiceCredentials getCredentials() {
        return credentials;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URI getEndpoint() {
        return this.endpoint;
    }

    public AsyncResult<V> execute(T reqObject, AsyncCallback<V> asyncHandler)
            throws ClientException, ServiceException {
        return this.executeWithCustomHeaders(reqObject, asyncHandler, null);
    }

    public AsyncResult<V> executeWithCustomHeaders(T reqObject, AsyncCallback<V> asyncHandler,
                                                   Map<String, String> customHeaders)
            throws ClientException, ServiceException {
        RequestMessage request = buildRequestMessage(reqObject);
        request.setMethod(this.getMethod());
        this.addRequiredHeader(request);
        this.addCustomHeader(request, customHeaders);
        this.addSignatureHeader(request);

        HttpCallback<V> callback = new HttpCallback<V>(
                this.buildResultParser(), this.buildExceptionParser(), asyncHandler);
        AsyncResult<V> asyncResult = callback.getAsyncResult();
        asyncResult.setTimewait(this.client.getClientConfiguration().getSocketTimeout());
        Future<HttpResponse> future = client.sendRequest(request, new ExecutionContext(), callback);
        asyncResult.setFuture(future);
        return asyncResult;
    }

    public V execute(T reqObject) throws ClientException, ServiceException {
        return this.executeWithCustomHeaders(reqObject, null);
    }

    public V executeWithCustomHeaders(T reqObject, Map<String, String> customHeaders)
            throws ClientException, ServiceException {
        AsyncResult<V> result = executeWithCustomHeaders(reqObject, null, customHeaders);
        V value = result.getResult();
        if (result.isSuccess()) {
            return value;
        }

        if (result.getException() instanceof ClientException) {
            throw (ClientException) result.getException();
        } else if (result.getException() instanceof ServiceException) {
            throw (ServiceException) result.getException();
        } else {
            ClientException ce = new ClientException(result.getException().toString(),
                    null, result.getException());
            ce.setStackTrace(result.getException().getStackTrace());
            throw ce;
        }
    }

    private void addCustomHeader(RequestMessage request, Map<String, String> customHeaders) {
        if (customHeaders == null || customHeaders.size() == 0) {
            return;
        }
        for (String key : customHeaders.keySet()) {
            request.getHeaders().put(key, customHeaders.get(key));
        }
    }

    protected void addRequiredHeader(RequestMessage request) {
        request.getHeaders().put(Constants.X_HEADER_API_VERSION,
                Constants.X_HEADER_API_VERSION_VALUE);

        if (request.getHeaders().get(Constants.DATE) == null) {
            request.getHeaders().put(Constants.DATE,
                    DateUtil.formatRfc822Date(new Date()));
        }

        if (request.getHeaders().get(Constants.CONTENT_TYPE) == null) {
            request.getHeaders().put(Constants.CONTENT_TYPE,
                    Constants.DEFAULT_CONTENT_TYPE);
        }
    }

    protected void addSignatureHeader(RequestMessage request)
            throws ClientException {
        if (credentials != null && credentials.getAccessKeyId() != null
                && credentials.getAccessKeySecret() != null) {
            // Add signature
            request.addHeader(Constants.AUTHORIZATION,
                    "MQ " + credentials.getAccessKeyId() + ":"
                            + getSignature(request)
            );

            // add security_token if security token is not empty.
            String securityToken = credentials.getSecurityToken();
            if (securityToken != null && !"".equals(securityToken)) {
                request.addHeader(Constants.SECURITY_TOKEN, securityToken);
            }
        }
    }

    private String getRelativeResourcePath(String subPath) {
        String rootPath = endpoint.getPath();
        if (subPath != null && !"".equals(subPath.trim())) {
            if (subPath.startsWith("/")) {
                subPath = subPath.substring(1);
            }
            if (!rootPath.endsWith("/")) {
                return rootPath + "/" + subPath;
            }
            return rootPath + subPath;
        }
        return rootPath;
    }

    private String getSignature(RequestMessage request) throws ClientException {
        Map<String, String> headers = request.getHeaders();

        StringBuffer canonicalizedHeaders = new StringBuffer();
        StringBuffer stringToSign = new StringBuffer();
        String contentMd5 = safeGetHeader(Constants.CONTENT_MD5, headers);
        String contentType = safeGetHeader(Constants.CONTENT_TYPE, headers);
        String date = safeGetHeader(Constants.DATE, headers);
        String canonicalizedResource = getRelativeResourcePath(request
                .getResourcePath());

        TreeMap<String, String> tmpHeaders = sortHeader(request.getHeaders());
        if (tmpHeaders.size() > 0) {
            Set<String> keySet = tmpHeaders.keySet();
            for (String key : keySet) {
                if (key.toLowerCase().startsWith(
                        Constants.X_HEADER_PREFIX)) {
                    canonicalizedHeaders.append(key).append(":")
                            .append(tmpHeaders.get(key)).append("\n");
                }
            }
        }
        stringToSign.append(method).append("\n").append(contentMd5)
                .append("\n").append(contentType).append("\n").append(date)
                .append("\n").append(canonicalizedHeaders)
                .append(canonicalizedResource);
        String signature;

        try {
            signature = ServiceSignature.create().computeSignature(
                    credentials.getAccessKeySecret(), stringToSign.toString());
        } catch (Exception e) {
            throw new ClientException("Signature fail", null, e);
        }

        return signature;
    }

    protected RequestMessage buildRequestMessage(T reqObject)
            throws ClientException {
        RequestMessage request = buildRequest(reqObject);
        String requestPath = request.getResourcePath();
        if (requestPath != null && (requestPath.startsWith("http://") || requestPath.startsWith("https://"))) {
            if (!requestPath.startsWith(endpoint.toString())) {
                throw new IllegalArgumentException("Endpoint["
                        + endpoint.toString() + "]和访问地址[" + requestPath
                        + "]不匹配.");
            } else {
                requestPath = requestPath.substring(endpoint.toString().length());
                if (requestPath.startsWith("/"))
                    requestPath = requestPath.substring(1);
                request.setResourcePath(requestPath);
            }
        }
        request.setEndpoint(endpoint);

        return request;
    }

    protected ResultParser<V> buildResultParser() {
        return null;
    }

    protected ResultParser<Exception> buildExceptionParser() {
        return new ExceptionResultParser(null);
    }

    protected abstract RequestMessage buildRequest(T reqObject)
            throws ClientException;
}
