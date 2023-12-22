package balance;

import protocol.Request;

import java.util.List;

/**
 * Abstract class for a load balancing policy
 *
 * @author sysu
 * @createTime 2023年12月22日
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String selectServiceAddress(List<String> serviceAddresses, Request request) {
        if (serviceAddresses.isEmpty()) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses, request);
    }

    protected abstract String doSelect(List<String> serviceAddresses, Request request);

}