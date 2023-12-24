package com.sysu.client;

import common.pojo.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import protocol.Response;
import service.OpenServiceA;
import stub.ClientStub;
import transport.netty.client.NettyClient;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/client")
public class Client {
    ClientStub client = new ClientStub(new NettyClient());

    OpenServiceA proxyA = client.getProxy(OpenServiceA.class);

    @RequestMapping("/sendRequest")
    public Object start() throws ExecutionException, InterruptedException {
        CompletableFuture<Object> user = proxyA.queryUserAsync(new Random().nextInt(0, 10));
        Response response = (Response) user.get();
        return response.getData();
    }
}
