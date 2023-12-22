package balance;

import protocol.Request;

import java.util.List;

public interface LoadBalance {
    String selectServiceAddress(List<String> serviceAddresses, Request request);
}
