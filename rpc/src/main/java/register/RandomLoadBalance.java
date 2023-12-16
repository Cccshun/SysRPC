package register;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;

@Slf4j
public class RandomLoadBalance implements LoadBalance{
    @Override
    public String balance(List<String> addressList) {
        Random random = new Random();
        int index = random.nextInt(addressList.size());
        log.info(index + "=="+ addressList.get(index));
        return addressList.get(index);
    }
}
