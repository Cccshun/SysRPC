package io;

import common.message.Request;
import common.message.Response;
import lombok.AllArgsConstructor;
import register.Register;
import register.ZkRegister;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@AllArgsConstructor
public class SimpleClient implements RPCClient {
    private final Register register;

    public SimpleClient() {
        register = new ZkRegister();
    }

    @Override
    public Response sendRequest(Request request) {
        InetSocketAddress address = register.serviceDiscovery(request.getInterfaceName());
        try {
            Socket socket = new Socket(address.getHostName(), address.getPort());
            // seed request message
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(request);
            oos.flush();

            // receive response message
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            return (Response) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
