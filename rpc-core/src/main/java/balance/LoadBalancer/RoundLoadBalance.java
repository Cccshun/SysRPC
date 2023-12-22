package balance.LoadBalancer;

import balance.AbstractLoadBalance;
import protocol.Request;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * implementation of round load balancing strategy
 *
 * @author sysu
 * @createTime 2023年12月22日
 */

public class RoundLoadBalance extends AbstractLoadBalance {
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    protected String doSelect(List<String> serviceAddresses, Request request) {
        return serviceAddresses.get(index.getAndAdd(1) % (serviceAddresses.size()));
    }
}
