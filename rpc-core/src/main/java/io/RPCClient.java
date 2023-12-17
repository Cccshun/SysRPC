package io;

import protocol.Request;
import protocol.Response;

public interface RPCClient {
    Response sendRequest(Request request, int serialization);

    void stop();
}
