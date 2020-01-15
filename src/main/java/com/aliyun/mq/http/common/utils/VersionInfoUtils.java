package com.aliyun.mq.http.common.utils;

public class VersionInfoUtils {
    private static String version = "1.0.3";

    private static String defaultUserAgent = null;

    public static String getDefaultUserAgent() {
        if (defaultUserAgent == null) {
            String platformInfo = System.getProperty("os.name") + "/"
                    + System.getProperty("os.version") + "/"
                    + System.getProperty("os.arch") + ";"
                    + System.getProperty("java.version");

            defaultUserAgent = "mq-java-sdk" + "/" + version + "("
                    + platformInfo + ")";
        }
        return defaultUserAgent;
    }
}
