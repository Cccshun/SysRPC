package transport.netty.client;

import protocol.Response;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache unprocessed Requests
 * */
public class RequestsCache {
    private static final Map<String, CompletableFuture<Response>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<Response> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    public void complete(Response response) {
        CompletableFuture<Response> future = UNPROCESSED_RESPONSE_FUTURES.remove(response.getRequestId());
        if (null != future) {
            future.complete(response);
        } else {
            throw new IllegalStateException();
        }
    }
}