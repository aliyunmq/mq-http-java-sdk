package com.aliyun.mq.http.common.http;

import java.util.HashMap;
import java.util.Map;

public class ServiceClientFactory {

    private static final Map<ClientConfiguration, ServiceClient> serviceClientMap = new HashMap<ClientConfiguration, ServiceClient>();

    public static ServiceClient createServiceClient(ClientConfiguration config) {
        synchronized (serviceClientMap) {
            ServiceClient serviceClient = serviceClientMap.get(config);
            if (serviceClient == null) {
                serviceClient = new DefaultServiceClient(config);
                serviceClientMap.put(config, serviceClient);
            } else {
                serviceClient.ref();
            }
            return serviceClient;
        }
    }

    public static void closeServiceClient(ServiceClient serviceClient) {
        synchronized (serviceClientMap) {
            int count = serviceClient.unRef();
            if (count <= 0) {
                serviceClientMap.remove(serviceClient.getClientConfigurationNoClone());
            }
        }
    }

    public static int getServiceClientCount() {
        synchronized (serviceClientMap) {
            return serviceClientMap.size();
        }
    }
}
