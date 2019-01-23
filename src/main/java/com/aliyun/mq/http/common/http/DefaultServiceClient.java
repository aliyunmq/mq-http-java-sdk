package com.aliyun.mq.http.common.http;

import com.aliyun.mq.http.common.ClientErrorCode;
import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.comm.RetryStrategy;
import com.aliyun.mq.http.common.comm.ExecutionContext;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.nio.client.HttpAsyncClient;

import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The default implementation of <code>ServiceClient</code>.
 */
public class DefaultServiceClient extends ServiceClient {

    AtomicBoolean clientIsOpen = new AtomicBoolean(false);
    private HttpAsyncClient httpClient;
    private PoolingNHttpClientConnectionManager connManager;

    private AtomicInteger refCount = new AtomicInteger(0);

    private ScheduledExecutorService timer = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "CheckApacheClientTimer");
        }
    });

    // this constructor in package visible
    DefaultServiceClient(ClientConfiguration config) {
        super(config);
        connManager = HttpFactory.createConnectionManager(config);
        httpClient = HttpFactory.createHttpAsyncClient(connManager, config);
        this.ref();
    }

    @Override
    int ref() {
        this.open();
        return refCount.incrementAndGet();
    }

    @Override
    int unRef() {
        if (refCount.decrementAndGet() <= 0) {
            this.close();
        }
        return refCount.get();
    }

    @Override
    public <T> Future<HttpResponse> sendRequestCore(
            ServiceClient.Request request, ExecutionContext context,
            HttpCallback<T> callback) throws IOException {
        assert request != null && context != null;

        if (!isOpen()) {
            throw new IOException("Http selector is not running, try it again after 3s.");
        }

        HttpRequestBase httpRequest = HttpFactory.createHttpRequest(request, context);

        return httpClient.execute(httpRequest, callback);
    }

    private void open() {
        if (this.httpClient != null
                && this.httpClient instanceof CloseableHttpAsyncClient
                && clientIsOpen.compareAndSet(false, true)) {
            ((CloseableHttpAsyncClient) httpClient).start();
            // start a thread to clean idle and expired connection
            HttpFactory.IdleConnectionMonitor.getInstance().addConnMgr(connManager);
        }

        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (refCount.get() > 0 && clientIsOpen.get()) {
                        // selector thread may be stopped because of unknown cases.
                        // this is to re create it & start it.
                        if (!isSelectorOk()) {
                            try {
                                connManager.shutdown();
                                if (httpClient instanceof CloseableHttpAsyncClient) {
                                    ((CloseableHttpAsyncClient) httpClient).close();
                                }
                                HttpFactory.IdleConnectionMonitor.getInstance().removeConnMgr(connManager);
                            } catch (Throwable e) {
                            }

                            connManager = HttpFactory.createConnectionManager(getClientConfigurationNoClone());
                            httpClient = HttpFactory.createHttpAsyncClient(connManager, getClientConfigurationNoClone());

                            ((CloseableHttpAsyncClient) httpClient).start();
                            // start a thread to clean idle and expired connection
                            HttpFactory.IdleConnectionMonitor.getInstance().addConnMgr(connManager);
                        }

                    }
                } catch (Throwable e) {
                }
            }
        }, 10 * 1000, 3 * 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isOpen() {
        return clientIsOpen.get() && isSelectorOk();
    }

    @Override
    protected boolean isSelectorOk() {
        if (this.httpClient instanceof CloseableHttpAsyncClient) {
            return ((CloseableHttpAsyncClient) httpClient).isRunning();
        }
        return true;
    }

    @Override
    protected void close() {
        HttpFactory.IdleConnectionMonitor.getInstance().removeConnMgr(connManager);
        if (this.httpClient != null
                && this.httpClient instanceof CloseableHttpAsyncClient
                && clientIsOpen.compareAndSet(true, false)) {
            try {
                ((CloseableHttpAsyncClient) httpClient).close();
                this.timer.shutdownNow();
            } catch (IOException e) { // quietly
            }
        }
    }

    protected RetryStrategy getDefaultRetryStrategy() {
        return new DefaultRetryStrategy();
    }

    private static class DefaultRetryStrategy extends RetryStrategy {

        @Override
        public boolean shouldRetry(Exception ex, RequestMessage request,
                                   ResponseMessage response, int retries) {
            if (ex instanceof ClientException) {
                String errorCode = ((ClientException) ex).getErrorCode();
                if (errorCode.equals(ClientErrorCode.CONNECTION_TIMEOUT)
                        || errorCode.equals(ClientErrorCode.SOCKET_TIMEOUT)) {
                    return true;
                }
            }

            if (response != null) {
                int statusCode = response.getStatusCode();
                if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR
                        || statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                    return true;
                }
            }

            return false;
        }
    }
}
