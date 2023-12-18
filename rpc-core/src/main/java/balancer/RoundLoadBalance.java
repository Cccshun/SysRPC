package balancer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundLoadBalance implements LoadBalance {
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public String balance(List<String> addressList) {
        return addressList.get(index.getAndAdd(1) % (addressList.size()));
    }
}
