package com.aliyun.mq.http.common.auth;

import com.aliyun.mq.http.common.utils.CodingUtils;

public class ServiceCredentials {
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;

    public ServiceCredentials() {
    }

    public ServiceCredentials(String accessKeyId, String accessKeySecret, String securityToken) {
        setAccessKeyId(accessKeyId);
        setAccessKeySecret(accessKeySecret);
        setSecurityToken(securityToken);
    }

    public ServiceCredentials(String accessKeyId, String accessKeySecret) {
        this(accessKeyId, accessKeySecret, "");
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        CodingUtils.assertParameterNotNull(accessKeyId, "accessKeyId");
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        CodingUtils.assertParameterNotNull(accessKeySecret, "accessKeySecret");

        this.accessKeySecret = accessKeySecret;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ServiceCredentials{");
        sb.append("accessKeyId='").append(accessKeyId).append('\'');
        sb.append(", accessKeySecret='").append(accessKeySecret).append('\'');
        sb.append(", securityToken='").append(securityToken).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
