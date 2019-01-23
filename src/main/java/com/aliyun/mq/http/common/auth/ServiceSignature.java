package com.aliyun.mq.http.common.auth;

public abstract class ServiceSignature {
    public ServiceSignature() {
    }

    public static ServiceSignature create() {
        return new HmacSHA1Signature();
    }

    public abstract String getAlgorithm();

    public abstract String getVersion();

    public abstract String computeSignature(String key, String data);
}