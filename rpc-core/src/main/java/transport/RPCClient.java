package transport;

import protocol.Request;

public interface RPCClient {
    Object sendRequest(Request request, int serialization);

    void stop();
}
