package service.impl;

import common.pojo.User;
import lombok.extern.slf4j.Slf4j;
import service.OpenServiceA;

import java.util.Random;

@Slf4j
public class OpenServiceAImpl implements OpenServiceA {
    @Override
    public User queryUser(int id) {
        log.info(Thread.currentThread().getName() + ": queryUser, id:{}", id);
        return new User("zhangsan", id, new Random().nextInt(20));
    }

    @Override
    public void insertUser(User user) {
        log.info(Thread.currentThread().getName() + ": insertUser, user:{}", user);
    }
}
