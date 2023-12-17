package test.server;

import io.NettyServer;
import service.impl.OpenServiceAImpl;
import service.impl.OpenServiceBImpl;
import stub.ServiceProvider;

public class ServerTest {
    public static void main(String[] args) {
        ServiceProvider serviceProvider = new ServiceProvider("localhost", 8088);
        serviceProvider.providerServiceInterface(new OpenServiceAImpl());
        serviceProvider.providerServiceInterface(new OpenServiceBImpl());
        NettyServer nettyServer = new NettyServer(serviceProvider);
        nettyServer.start(8088);
    }
}
