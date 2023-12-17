package test.client;

import common.pojo.Blog;
import common.pojo.User;
import io.NettyClient;
import lombok.extern.slf4j.Slf4j;
import service.OpenServiceA;
import service.OpenServiceB;
import stub.ClientStub;

import java.util.Random;

@Slf4j
public class ClientTest {
    public static void main(String[] args) {
        ClientStub client = new ClientStub(new NettyClient());

        OpenServiceA proxyA = client.getProxy(OpenServiceA.class);
        User user = proxyA.queryUser(new Random().nextInt(0, 10));
        proxyA.insertUser(user);
        log.info(user.toString());

        OpenServiceB proxyB = client.getProxy(OpenServiceB.class);
        Blog blog = proxyB.queryBlog(new Random().nextInt(0, 10));
        proxyB.insertBlog(blog);
        log.info(blog.toString());
        client.shutdown();
    }
}
