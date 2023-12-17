package service;

import common.pojo.User;

public interface OpenServiceA {
    User queryUser(int id);
    void insertUser(User user);
}
