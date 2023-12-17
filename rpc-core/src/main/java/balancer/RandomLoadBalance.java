package balancer;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {
    @Override
    public String balance(List<String> addressList) {
        Random random = new Random();
        int index = random.nextInt(addressList.size());
        return addressList.get(index);
    }
}
