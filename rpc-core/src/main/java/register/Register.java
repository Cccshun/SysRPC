package register;

import java.net.InetSocketAddress;

/**
 * 服务注册发现
*/

public interface Register {
    void serviceRegister(String serviceName, InetSocketAddress serviceAddress);
    InetSocketAddress serviceDiscovery(String serviceName);
}
