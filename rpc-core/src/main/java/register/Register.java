package register;

import protocol.Request;

import java.net.InetSocketAddress;

/**
 * 服务注册发现
*/

public interface Register {
    void serviceRegister(String serviceName, InetSocketAddress serviceAddress);
    InetSocketAddress serviceDiscovery(Request request);
}
