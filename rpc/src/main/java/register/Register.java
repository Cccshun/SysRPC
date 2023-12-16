package register;

import java.net.InetSocketAddress;

public interface Register {
    void serviceRegister(String serviceName, InetSocketAddress serviceAddress);
    InetSocketAddress serviceDiscovery(String serviceName);
}
