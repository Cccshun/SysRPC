package balancer;

import java.util.List;

public class RoundLoadBalance implements LoadBalance {
    private int index = 0;

    @Override
    public String balance(List<String> addressList) {
        index++;
        return addressList.get(index % (addressList.size()));
    }
}
