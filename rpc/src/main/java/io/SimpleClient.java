package io;

import common.message.Request;
import common.message.Response;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@AllArgsConstructor
public class SimpleClient implements RPCClient {
    private String host;
    private int port;
    @Override
    public Response sendRequest(Request request) {
        try {
            Socket socket = new Socket(host, port);
            // 发送调用请求消息体
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(request);
            oos.flush();

            // 接收调用结果
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            return (Response) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
