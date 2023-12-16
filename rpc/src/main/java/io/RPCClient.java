package io;

import common.message.Request;
import common.message.Response;

public interface RPCClient {
    Response sendRequest(Request request);
}
