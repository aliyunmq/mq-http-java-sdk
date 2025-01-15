package com.aliyun.mq.http.common.utils;

import com.aliyun.auth.credentials.ICredential;
import com.aliyun.mq.http.common.auth.ServiceCredentials;

public class ServiceCredentialsWrapper {
    public static ServiceCredentials WrapServiceCredentials(ICredential credential) {
        return new ServiceCredentials(credential.accessKeyId(), credential.accessKeySecret(), credential.securityToken());
    }
}
