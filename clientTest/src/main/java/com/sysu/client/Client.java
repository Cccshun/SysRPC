package com.sysu.client;

import common.pojo.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.OpenServiceA;
import stub.ClientStub;
import transport.netty.client.NettyClient;

import java.util.Random;

@RestController
@RequestMapping("/client")
public class Client {
    ClientStub client = new ClientStub(new NettyClient());

    OpenServiceA proxyA = client.getProxy(OpenServiceA.class);

    @RequestMapping("/sendRequest")
    public Object start() {

        User user = proxyA.queryUser(new Random().nextInt(0, 10));
//        proxyA.insertUser(user);
//        log.info(user.toString());
//
//        OpenServiceB proxyB = client.getProxy(OpenServiceB.class);
//        Blog blog = proxyB.queryBlog(new Random().nextInt(0, 10));
//        proxyB.insertBlog(blog);
//        log.info(blog.toString());
//        client.shutdown();
        return user;
    }
}
