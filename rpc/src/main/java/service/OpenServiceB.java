package service;

import common.pojo.Blog;

public interface OpenServiceB {
    Blog queryBlog(int id);

    void insertBlog(Blog blog);
}
