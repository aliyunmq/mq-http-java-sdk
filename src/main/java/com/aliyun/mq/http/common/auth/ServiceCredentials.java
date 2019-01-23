package com.aliyun.mq.http.common.auth;

import com.aliyun.mq.http.common.utils.CodingUtils;

/**
 * 表示用户访问的授权信息。
 */
public class ServiceCredentials {
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;

    /**
     * 构造函数。
     */
    public ServiceCredentials() {
    }

    /**
     * 构造函数。
     *
     * @param accessKeyId     Access Key ID.
     * @param accessKeySecret Access Key Secret.
     * @param securityToken   security temp token. (optional)
     * @throws NullPointerException accessKeyId.accessKeySecret.....
     */
    public ServiceCredentials(String accessKeyId, String accessKeySecret, String securityToken) {
        setAccessKeyId(accessKeyId);
        setAccessKeySecret(accessKeySecret);
        setSecurityToken(securityToken);
    }

    /**
     * 构造函数。
     *
     * @param accessKeyId     Access Key ID。
     * @param accessKeySecret Access Key Secret。
     * @throws NullPointerException accessKeyId或accessKeySecret为空指针。
     */
    public ServiceCredentials(String accessKeyId, String accessKeySecret) {
        this(accessKeyId, accessKeySecret, "");
    }

    /**
     * 获取访问用户的Access Key ID。
     *
     * @return Access Key ID。
     */
    public String getAccessKeyId() {
        return accessKeyId;
    }

    /**
     * 设置访问用户的Access ID。
     *
     * @param accessKeyId Access Key ID。
     */
    public void setAccessKeyId(String accessKeyId) {
        CodingUtils.assertParameterNotNull(accessKeyId, "accessKeyId");
        this.accessKeyId = accessKeyId;
    }

    /**
     * 获取访问用户的Access Key Secret。
     *
     * @return Access Key Secret。
     */
    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    /**
     * 设置访问用户的Access Key Secret。
     *
     * @param accessKeySecret Access Key Secret。
     */
    public void setAccessKeySecret(String accessKeySecret) {
        CodingUtils.assertParameterNotNull(accessKeySecret, "accessKeySecret");

        this.accessKeySecret = accessKeySecret;
    }

    /**
     * 获取security token。
     *
     * @return security token
     */
    public String getSecurityToken() {
        return securityToken;
    }

    /**
     * 设置访问用户的security token
     *
     * @param securityToken token.
     */
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
