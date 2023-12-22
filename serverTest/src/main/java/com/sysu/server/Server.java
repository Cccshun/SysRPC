package com.sysu.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.impl.OpenServiceAImpl;
import service.impl.OpenServiceBImpl;
import stub.ServiceProvider;
import transport.netty.server.NettyServer;

@Component
public class Server {

    @Autowired
    public void start() {
        ServiceProvider serviceProvider = new ServiceProvider("localhost", 8087);
        serviceProvider.providerServiceInterface(new OpenServiceAImpl());
        serviceProvider.providerServiceInterface(new OpenServiceBImpl());
        NettyServer nettyServer = new NettyServer(serviceProvider);
        nettyServer.start(8087);
    }
}
