package com.aliyun.mq.http.common.http;

import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.HttpMethod;
import com.aliyun.mq.http.common.utils.HttpHeaders;
import com.aliyun.mq.http.common.utils.VersionInfoUtils;
import com.aliyun.mq.http.common.comm.ExecutionContext;
import com.aliyun.mq.http.common.comm.RepeatableInputStreamEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * The factory to create HTTP-related objects.
 */
public class HttpFactory {

    private static SSLConnectionSocketFactory getSSLSocketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, trustAllCerts, null);
            SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(
                    sslcontext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return ssf;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PoolingNHttpClientConnectionManager createConnectionManager(ClientConfiguration config) {
        // Set HTTP params.
        // Create I/O reactor configuration
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setIoThreadCount(config.getIoReactorThreadCount())
                .setConnectTimeout(config.getConnectionTimeout())
                .setSoTimeout(config.getSocketTimeout())
                .setSoKeepAlive(config.isSoKeepAlive()).build();

        // Create a custom I/O reactort
        ConnectingIOReactor ioReactor;
        try {
            ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        } catch (IOReactorException e) {
            throw new RuntimeException(e);
        }

        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(
                ioReactor);
        connManager.setDefaultMaxPerRoute(config.getMaxConnectionsPerRoute());
        connManager.setMaxTotal(config.getMaxConnections());

        return connManager;
    }

    public static CloseableHttpAsyncClient createHttpAsyncClient(
            PoolingNHttpClientConnectionManager connManager,
            ClientConfiguration config) {
        HttpAsyncClientBuilder httpClientBuilder = HttpAsyncClients.custom()
                .setConnectionManager(connManager);

        // Set proxy if set.
        String proxyHost = config.getProxyHost();
        int proxyPort = config.getProxyPort();

        if (proxyHost != null && proxyPort > 0) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);

            httpClientBuilder.setProxy(proxy);

            String proxyUsername = config.getProxyUsername();
            String proxyPassword = config.getProxyPassword();

            if (proxyUsername != null && proxyPassword != null) {
                String proxyDomain = config.getProxyDomain();
                String proxyWorkstation = config.getProxyWorkstation();

                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

                credentialsProvider.setCredentials(new AuthScope(proxy),
                        new NTCredentials(proxyUsername, proxyPassword,
                                proxyWorkstation, proxyDomain)
                );

                httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider);

            }
        }

        RequestConfig defaultRequestConfig = RequestConfig
                .custom()
                .setCookieSpec(CookieSpecs.BEST_MATCH)
                .setExpectContinueEnabled(true)
                .setStaleConnectionCheckEnabled(true)
                .setTargetPreferredAuthSchemes(
                        Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                .setConnectTimeout(config.getConnectionTimeout())
                .setSocketTimeout(config.getSocketTimeout())
                .setExpectContinueEnabled(config.isExceptContinue()).build();

        httpClientBuilder.setDefaultRequestConfig(defaultRequestConfig);
        httpClientBuilder
                .setMaxConnPerRoute(config.getMaxConnectionsPerRoute());
        httpClientBuilder.setMaxConnTotal(config.getMaxConnections());
        httpClientBuilder.setUserAgent(VersionInfoUtils.getDefaultUserAgent());
        CloseableHttpAsyncClient httpclient = httpClientBuilder.build();

        return httpclient;
    }

    public static HttpRequestBase createHttpRequest(ServiceClient.Request request,
                                                    ExecutionContext context) {

        String uri = request.getUri();
        HttpMethod method = request.getMethod();
        HttpRequestBase httpRequest;
        if (method == HttpMethod.POST) {
            // POST
            HttpPost postMethod = new HttpPost(uri);

            if (request.getContent() != null) {
                postMethod.setEntity(new RepeatableInputStreamEntity(request));
            }

            httpRequest = postMethod;
        } else if (method == HttpMethod.PUT) {
            // PUT
            HttpPut putMethod = new HttpPut(uri);

            if (request.getContent() != null) {
                putMethod.setEntity(new RepeatableInputStreamEntity(request));
            }

            httpRequest = putMethod;
        } else if (method == HttpMethod.GET) {
            // GET
            httpRequest = new HttpGet(uri);
        } else if (method == HttpMethod.DELETE) {
            // DELETE
            //httpRequest = new HttpDelete(uri);
            // support body in Delete
            HttpDeleteWrapper deleteMethod = new HttpDeleteWrapper(uri);
            if (request.getContent() != null) {
                deleteMethod.setEntity(new RepeatableInputStreamEntity(request));
            }
            httpRequest = deleteMethod;
        } else if (method == HttpMethod.HEAD) {
            httpRequest = new HttpHead(uri);
        } else if (method == HttpMethod.OPTIONS) {
            httpRequest = new HttpOptions(uri);
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unsupported HTTP method: %s.", request.getMethod()
                            .toString()
            ));
        }

        configureRequestHeaders(request, context, httpRequest);
        // httpRequest.addHeader("User-Agent","aliyun-java-sdk");
        return httpRequest;
    }

    private static void configureRequestHeaders(ServiceClient.Request request,
                                                ExecutionContext context, HttpRequestBase httpRequest) {
        // Copy headers in the request message to the HTTP request
        for (Entry<String, String> entry : request.getHeaders().entrySet()) {
            // HttpClient fills in the Content-Length,
            // and complains if add it again, so skip it as well as the Host
            // header.
            if (entry.getKey().equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)
                    || entry.getKey().equalsIgnoreCase(HttpHeaders.HOST)) {
                continue;
            }

            httpRequest.addHeader(entry.getKey(), entry.getValue());
        }

        // Set content type and encoding
        // if (httpRequest.getHeaders(HttpHeaders.CONTENT_TYPE) == null ||
        // httpRequest.getHeaders(HttpHeaders.CONTENT_TYPE).length == 0){
        // httpRequest.addHeader(HttpHeaders.CONTENT_TYPE,
        // "application/x-www-form-urlencoded; " +
        // "charset=" + context.getCharset().toLowerCase());
        // }
    }

    public static class IdleConnectionMonitor extends Thread {
        private static final IdleConnectionMonitor instance = new IdleConnectionMonitor();
        private final List<NHttpClientConnectionManager> connMgrs = new ArrayList<NHttpClientConnectionManager>();
        private volatile boolean shutdown = false;

        private int CONNECTION_MANAGER_LIMIT = 50;

        private IdleConnectionMonitor() {
            this.setName("IdleConnectionMonitorThread");
            this.setDaemon(true);
            this.start();
        }

        public static IdleConnectionMonitor getInstance() {
            return instance;
        }

        public void addConnMgr(NHttpClientConnectionManager connMgr) {
            synchronized (connMgrs) {
                if (CONNECTION_MANAGER_LIMIT > 0 && connMgrs.size() > CONNECTION_MANAGER_LIMIT) {
                    throw new ClientException("Too Many ServiceClient created.", null);
                }
                connMgrs.add(connMgr);
            }
        }

        public void removeConnMgr(NHttpClientConnectionManager connMgr) {
            synchronized (connMgrs) {
                connMgrs.remove(connMgr);
            }
        }

        public void set_CONNECTION_MANAGER_LIMIT(int limit) {
            this.CONNECTION_MANAGER_LIMIT = limit;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    sleep(15000);
                    List<NHttpClientConnectionManager> tmpConnMgrs;
                    synchronized (connMgrs) {
                        tmpConnMgrs = new ArrayList<NHttpClientConnectionManager>(connMgrs);
                    }
                    for (NHttpClientConnectionManager connMgr : tmpConnMgrs) {
                        // Close expired connections
                        connMgr.closeExpiredConnections();
                        // Optionally, close connections
                        // that have been idle longer than 60 sec
                        connMgr.closeIdleConnections(60, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                // terminate
            }
        }

        synchronized public void shutdown() {
            shutdown = true;
        }

    }
}
