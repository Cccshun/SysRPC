package service;

import common.pojo.Blog;
import common.pojo.User;

import java.util.concurrent.CompletableFuture;

public interface OpenServiceB {
    Blog queryBlogSync(int id);

    default CompletableFuture<Object> queryBlogAsync(int id) {
        return CompletableFuture.completedFuture(queryBlogSync(id));
    }

    void insertBlogSync(Blog blog);

    default CompletableFuture<Void> insertUserAsync(Blog blog) {
        return CompletableFuture.runAsync(() -> insertBlogSync(blog));
    }
}
