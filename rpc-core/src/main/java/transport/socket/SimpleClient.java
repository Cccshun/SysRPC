package transport.socket;

import protocol.Request;
import lombok.AllArgsConstructor;
import protocol.Response;
import register.Register;
import register.ZkRegister;
import transport.RPCClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class SimpleClient implements RPCClient {
    private final Register register;
    private Socket socket;

    public SimpleClient() {
        register = new ZkRegister();
    }

    @Override
    public Object sendRequest(Request request, int serialization) {
        InetSocketAddress address = register.serviceDiscovery(request);
        try {
            socket = new Socket(address.getHostName(), address.getPort());
            // seed request message
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(request);
            oos.flush();

            // receive response message
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
