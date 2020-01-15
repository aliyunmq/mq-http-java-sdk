package com.aliyun.mq.http.common.utils;

import com.aliyun.mq.http.common.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class Utils {
    public static Log logger = LogFactory.getLog(Utils.class);

    public static URI getHttpURI(String endpoint) {
        if (endpoint == null) {
            logger.warn("Endpoint is null");
            throw new NullPointerException("Endpoint is null");
        }

        try {
            if (!endpoint.startsWith(Constants.HTTP_PREFIX) && !endpoint.startsWith(Constants.HTTPS_PREFIX)) {
                logger.warn("Only support http or https protocol.Endpoint must be started by http:// or https://.");
                throw new IllegalArgumentException("Only support http or https protocol。Endpoint must be started by http:// or https://.");
            }
            while (endpoint.endsWith(Constants.SLASH)) {
                endpoint = endpoint.substring(0, endpoint.length() - 1);
            }

            if (endpoint.length() < Constants.HTTP_PREFIX.length()) {
                logger.warn("Invalid endpoint.");
                throw new IllegalArgumentException("Invalid endpoint.");
            }
            return new URI(endpoint);

        } catch (URISyntaxException e) {
            logger.warn("uri syntax error");
            throw new IllegalArgumentException(e);
        }
    }
}
