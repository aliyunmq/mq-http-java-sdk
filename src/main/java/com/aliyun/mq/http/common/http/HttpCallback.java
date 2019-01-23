package com.aliyun.mq.http.common.http;

import com.aliyun.mq.http.model.AsyncCallback;
import com.aliyun.mq.http.model.AsyncResult;
import com.aliyun.mq.http.common.ClientException;
import com.aliyun.mq.http.common.parser.ResultParser;
import com.aliyun.mq.http.common.utils.HttpUtil;
import com.aliyun.mq.http.common.utils.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class HttpCallback<T> implements FutureCallback<HttpResponse> {
    private static Log log = LogFactory.getLog(HttpCallback.class);
    private boolean success = false;
    private Exception exception = null;
    private ResponseMessage responseMessage = null;
    private boolean cancalled = false;
    private ResultParser<T> resultParser;
    private AsyncCallback<T> callback;
    private DefaultAsyncResult<T> result;
    private ResultParser<Exception> exceptionParser;
    private static ExecutorService executor;

    static {
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ((ThreadPoolExecutor) executor).setKeepAliveTime(30, TimeUnit.SECONDS);
        ((ThreadPoolExecutor) executor).allowCoreThreadTimeOut(true);
    }

    public static void setCallbackExecutor(ExecutorService executor) {
        HttpCallback.executor = executor;
    }

    public HttpCallback(ResultParser<T> resultParser,
                        ResultParser<Exception> exceptionParser,
                        AsyncCallback<T> callback) {
        this.resultParser = resultParser;
        this.callback = callback;
        this.exceptionParser = exceptionParser;
        this.result = new DefaultAsyncResult<T>(this);
    }

    private void executeCallback(final AsyncCallback<T> callback, final T result) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(result);
            }
        });
    }

    private void executeCallback(final AsyncCallback<T> callback, final Exception ex) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                callback.onFail(ex);
            }
        });
    }

    @Override
    public void completed(HttpResponse response) {
        try {
            buildResponseMessage(response);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            if (callback != null) {
                executeCallback(callback, ex);
            }
            result.onFail(ex);
            log.error("onFail finish when exception in completed");
        }
    }

    protected void buildResponseMessage(HttpResponse response) {
        // Build result
        responseMessage = new ResponseMessage();
        // message.setUrl(request.getUri());
        if (response.getStatusLine() != null) {
            responseMessage.setStatusCode(response.getStatusLine()
                    .getStatusCode());
        }

        if (response.getEntity() != null) {
            try {
                responseMessage.setContent(response.getEntity().getContent());
            } catch (IllegalStateException e) {
                log.error(e.getMessage(), e);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        // fill in headers
        Header[] headers = response.getAllHeaders();
        Map<String, String> resultHeaders = new HashMap<String, String>();
        for (int i = 0; i < headers.length; i++) {
            Header h = headers[i];
            resultHeaders.put(h.getName(), h.getValue());
        }
        HttpUtil.convertHeaderCharsetFromIso88591(resultHeaders);
        responseMessage.setHeaders(resultHeaders);

        handleResult();
    }

    private void close() {
        if (responseMessage != null) {
            try {
                this.responseMessage.close();
            } catch (IOException e) {
            }
        }

    }

    private void handleResult() {
        try {
            if (responseMessage.isSuccessful()) {
                T obj = null;
                if (resultParser != null) {
                    obj = this.resultParser.parse(responseMessage);
                }
                if (callback != null) {
                    executeCallback(callback, obj);
                }

                result.onSuccess(obj);
                this.success = true;
            } else {
                Exception obj = exceptionParser.parse(responseMessage);
                if (callback != null) {
                    executeCallback(callback, obj);
                }

                result.onFail(obj);

            }
        } catch (Exception ex) {
            try {
                System.out.println(IOUtils.readStreamAsString(responseMessage.getContent(), "UTF-8"));
            } catch (Exception e) {
            }
            if (callback != null) {
                executeCallback(callback, ex);
            }
            result.onFail(ex);
        }

    }

    @Override
    public void failed(Exception ex) {
        this.exception = ex;
        try {
            if (callback != null) {
                executeCallback(callback, ex);
            }
            result.onFail(ex);
        } catch (Exception e) {
            if (callback != null) {
                executeCallback(callback, ex);
            }
            result.onFail(e);
        }
    }

    @Override
    public void cancelled() {
        this.cancalled = true;
        exception = new ClientException("call is cancelled.", null);
        try {
            if (callback != null) {
                executeCallback(callback, exception);
            }
            result.onFail(exception);
        } catch (Exception e) {
            if (callback != null) {
                executeCallback(callback, e);
            }
            result.onFail(e);
        }
    }

    public boolean isCancelled() {
        return cancalled;
    }

    public boolean isSuccess() {
        return success;
    }

    public Exception getException() {
        return this.exception;
    }

    public ResponseMessage getResponseMessage() {
        return responseMessage;
    }

    public AsyncResult<T> getAsyncResult() {
        return this.result;
    }

    static class DefaultAsyncResult<T> implements AsyncResult<T> {
        private ReentrantLock rlock = new ReentrantLock();
        private Condition lock = rlock.newCondition();

        private long defaultTimewait;
        private long startTimeMillis;

        private boolean completed = false;
        private T result = null;
        private boolean success;
        private Exception exception;

        private HttpCallback<T> callback;
        private Future<HttpResponse> future;

        protected DefaultAsyncResult(HttpCallback<T> callback) {
            this.callback = callback;
            this.startTimeMillis = System.currentTimeMillis();
        }

        public void setTimewait(long timewait) {
            defaultTimewait = timewait;
        }

        public void setFuture(Future<HttpResponse> future) {
            this.future = future;
        }

        /*
         * (non-Javadoc)
         *
         */
        @Override
        public T getResult() {
            T result = getResult(defaultTimewait);
            while (result == null && (this.future != null && !this.future.isDone())) {
                result = getResult(defaultTimewait);
            }
            return result;
        }

        /*
         * (non-Javadoc)
         *
         */
        @Override
        public T getResult(long timewait) {
            if (!completed) {
                try {
                    rlock.lock();
                    if (!completed) {
                        boolean signaled = false;
                        if (timewait <= 0) {
                            signaled = lock.await(defaultTimewait, TimeUnit.MILLISECONDS);
                        } else {
                            signaled = lock.await(timewait, TimeUnit.MILLISECONDS);
                        }
                        if (!signaled
                                && (this.future != null && this.future.isDone())
                                && System.currentTimeMillis() >= (startTimeMillis + defaultTimewait)) {
                            this.exception = new ClientException("Client wait result timeout!", null);
                            this.success = false;
                            this.completed = true;
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    rlock.unlock();
                }
            }
            return result;
        }

        public void onSuccess(T result) {
            try {
                rlock.lock();
                if (completed) {
                    return;
                }
                this.result = result;
                this.success = true;
                this.completed = true;
                lock.signal();
            } finally {
                rlock.unlock();
            }
        }

        public void onFail(Exception ex) {
            try {
                rlock.lock();
                if (completed) {
                    return;
                }
                this.exception = ex;
                this.success = false;
                this.completed = true;
                lock.signal();
            } finally {
                rlock.unlock();
            }
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        @Override
        public Exception getException() {
            return exception;
        }

        //TODO: erase it ?
        public void close() {
            callback.close();
        }
    }
}
