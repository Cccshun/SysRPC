package transport;

import protocol.Request;

import java.util.concurrent.ExecutionException;

public interface RPCClient {
    Object sendRequest(Request request, int serialization);

    void stop();
}
