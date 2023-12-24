package service.impl;

import common.pojo.Blog;
import lombok.extern.slf4j.Slf4j;
import service.OpenServiceB;

import java.util.Random;

@Slf4j
public class OpenServiceBImpl implements OpenServiceB {
    @Override
    public Blog queryBlogSync(int id) {
        log.info(Thread.currentThread().getName() + ": queryBlog, id:{}", id);
        return new Blog(id, new Random().nextInt(100), "<rpc blog>");
    }

    @Override
    public void insertBlogSync(Blog blog) {
        log.info(Thread.currentThread().getName() + ":insertBlog, blog:{}", blog);
    }
}
