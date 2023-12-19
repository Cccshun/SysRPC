package test.client;

import common.pojo.Blog;
import common.pojo.User;
import transport.netty.client.NettyClient;
import lombok.extern.slf4j.Slf4j;
import service.OpenServiceA;
import service.OpenServiceB;
import stub.ClientStub;

import java.sql.Time;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ClientTest {
    public static void main(String[] args) {
        ClientStub client = new ClientStub(new NettyClient());

        OpenServiceA proxyA = client.getProxy(OpenServiceA.class);
//        User user = proxyA.queryUser(new Random().nextInt(0, 10));
//        proxyA.insertUser(user);
//        log.info(user.toString());
//
        OpenServiceB proxyB = client.getProxy(OpenServiceB.class);
//        Blog blog = proxyB.queryBlog(new Random().nextInt(0, 10));
//        proxyB.insertBlog(blog);
//        log.info(blog.toString());
//        client.shutdown();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            proxyA.queryUser(new Random().nextInt(0, 10));
            proxyB.queryBlog(new Random().nextInt(0, 10));
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}
