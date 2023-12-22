package balance.LoadBalancer;

import balance.AbstractLoadBalance;
import protocol.Request;

import java.util.List;
import java.util.Random;

/**
 * implementation of random load balancing strategy
 *
 * @author sysu
 * @createTime 2023年12月22日
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected String doSelect(List<String> serviceAddresses, Request request) {
        Random random = new Random();
        int index = random.nextInt(serviceAddresses.size());
        return serviceAddresses.get(index);
    }
}
