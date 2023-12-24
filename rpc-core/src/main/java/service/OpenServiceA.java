package service;

import common.pojo.User;
import protocol.Response;

import java.util.concurrent.CompletableFuture;

public interface OpenServiceA {
    User queryUserSync(int id);

    default CompletableFuture<Object> queryUserAsync(int id) {
        return CompletableFuture.supplyAsync(()-> queryUserSync(id));
    }

    void insertUserSync(User user);

    default CompletableFuture<Void> insertUserAsync(User user) {
        return CompletableFuture.runAsync(() -> insertUserSync(user));
    }
}
