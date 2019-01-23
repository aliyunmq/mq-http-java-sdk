package com.aliyun.mq.http.common.http;

import com.aliyun.mq.http.common.utils.VersionInfoUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ClientConfiguration implements Cloneable {

    private static final String DEFAULT_USER_AGENT = VersionInfoUtils.getDefaultUserAgent();
    private String userAgent = DEFAULT_USER_AGENT;
    private static final int DEFAULT_MAX_RETRIES = 3;
    private int maxErrorRetry = DEFAULT_MAX_RETRIES;
    private String proxyHost;
    private int proxyPort;
    private String proxyUsername;
    private String proxyPassword;
    private String proxyDomain;
    private String proxyWorkstation;
    private int maxConnections = 4000;
    private int maxConnectionsPerRoute = 4000;
    private int socketTimeout = 33 * 1000;
    private int connectionTimeout = 30 * 1000;
    private boolean soKeepAlive = true;
    private int ioReactorThreadCount = Runtime.getRuntime().availableProcessors();

    private boolean exceptContinue = true;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(maxConnections);
        hcb.append(maxConnectionsPerRoute);
        hcb.append(socketTimeout);
        hcb.append(connectionTimeout);
        hcb.append(soKeepAlive);
        hcb.append(exceptContinue);
        hcb.append(proxyPort);
        hcb.append(proxyHost);
        hcb.append(proxyUsername);
        hcb.append(proxyPassword);
        hcb.append(proxyDomain);
        hcb.append(proxyWorkstation);
        return hcb.build();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClientConfiguration) {
            ClientConfiguration conf = (ClientConfiguration) obj;
            return maxConnections == conf.maxConnections
                    && maxConnectionsPerRoute == conf.maxConnectionsPerRoute
                    && socketTimeout == conf.socketTimeout
                    && connectionTimeout == conf.connectionTimeout
                    && proxyPort == conf.proxyPort
                    && proxyHost == null ? conf.proxyHost == null : proxyHost.equals(conf.proxyHost)
                    && proxyUsername == null ? conf.proxyUsername == null : proxyUsername.equals(conf.proxyUsername)
                    && proxyPassword == null ? conf.proxyPassword == null : proxyPassword.equals(conf.proxyPassword)
                    && proxyDomain == null ? conf.proxyDomain == null : proxyDomain.equals(conf.proxyDomain)
                    && proxyWorkstation == null ? conf.proxyWorkstation == null : proxyWorkstation.equals(conf.proxyWorkstation)
                    && soKeepAlive == conf.soKeepAlive
                    && exceptContinue == conf.exceptContinue;
        }
        return super.equals(obj);
    }

    /**
     * 构造新实例。
     */
    public ClientConfiguration() {
    }

    /**
     * 构造用户代理。
     *
     * @return 用户代理。
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * 设置用户代理。
     *
     * @param userAgent 用户代理。
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * 返回代理服务器主机地址。
     *
     * @return 代理服务器主机地址。
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * 设置代理服务器主机地址。
     *
     * @param proxyHost 代理服务器主机地址。
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * 返回代理服务器端口。
     *
     * @return 代理服务器端口。
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * 设置代理服务器端口。
     *
     * @param proxyPort 代理服务器端口。
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * 返回代理服务器验证的用户名。
     *
     * @return 用户名。
     */
    public String getProxyUsername() {
        return proxyUsername;
    }

    /**
     * 设置代理服务器验证的用户名。
     *
     * @param proxyUsername 用户名。
     */
    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    /**
     * 返回代理服务器验证的密码。
     *
     * @return 密码。
     */
    public String getProxyPassword() {
        return proxyPassword;
    }

    /**
     * 设置代理服务器验证的密码。
     *
     * @param proxyPassword 密码。
     */
    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    /**
     * 返回访问NTLM验证的代理服务器的Windows域名（可选）。
     *
     * @return 域名。
     */
    public String getProxyDomain() {
        return proxyDomain;
    }

    /**
     * 设置访问NTLM验证的代理服务器的Windows域名（可选）。
     *
     * @param proxyDomain 域名。
     */
    public void setProxyDomain(String proxyDomain) {
        this.proxyDomain = proxyDomain;
    }

    /**
     * 返回NTLM代理服务器的Windows工作站名称。
     *
     * @return NTLM代理服务器的Windows工作站名称。
     */
    public String getProxyWorkstation() {
        return proxyWorkstation;
    }

    /**
     * 设置NTLM代理服务器的Windows工作站名称。
     * （可选，如果代理服务器非NTLM，不需要设置该参数）。
     *
     * @param proxyWorkstation NTLM代理服务器的Windows工作站名称。
     */
    public void setProxyWorkstation(String proxyWorkstation) {
        this.proxyWorkstation = proxyWorkstation;
    }

    /**
     * 返回允许打开的最大HTTP连接数。
     *
     * @return 最大HTTP连接数。
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * 设置允许打开的最大HTTP连接数。
     *
     * @param maxConnections 最大HTTP连接数。
     */
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * 返回通过打开的连接传输数据的超时时间（单位：毫秒）。
     * 0表示无限等待（但不推荐使用）。
     *
     * @return 通过打开的连接传输数据的超时时间（单位：毫秒）。
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * 设置通过打开的连接传输数据的超时时间（单位：毫秒）。
     * 0表示无限等待（但不推荐使用）。
     *
     * @param socketTimeout 通过打开的连接传输数据的超时时间（单位：毫秒）。
     */
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    /**
     * 返回建立连接的超时时间（单位：毫秒）。
     *
     * @return 建立连接的超时时间（单位：毫秒）。
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * 设置建立连接的超时时间（单位：毫秒）。
     *
     * @param connectionTimeout 建立连接的超时时间（单位：毫秒）。
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * 返回一个值表示当可重试的请求失败后最大的重试次数。（默认值为3）
     *
     * @return 当可重试的请求失败后最大的重试次数。
     */
    public int getMaxErrorRetry() {
        return maxErrorRetry;
    }

    /**
     * 设置一个值表示当可重试的请求失败后最大的重试次数。（默认值为3）
     *
     * @param maxErrorRetry 当可重试的请求失败后最大的重试次数。
     */
    public void setMaxErrorRetry(int maxErrorRetry) {
        this.maxErrorRetry = maxErrorRetry;
    }

    public boolean isSoKeepAlive() {
        return soKeepAlive;
    }

    public void setSoKeepAlive(boolean soKeepAlive) {
        this.soKeepAlive = soKeepAlive;
    }

    public boolean isExceptContinue() {
        return exceptContinue;
    }

    public void setExceptContinue(boolean exceptContinue) {
        this.exceptContinue = exceptContinue;
    }

    public int getMaxConnectionsPerRoute() {
        return maxConnectionsPerRoute;
    }

    public void setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }

    public int getIoReactorThreadCount() {
        return ioReactorThreadCount;
    }

    public void setIoReactorThreadCount(int ioReactorThreadCount) {
        this.ioReactorThreadCount = ioReactorThreadCount;
    }
}
