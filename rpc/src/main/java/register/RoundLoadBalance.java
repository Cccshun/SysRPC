package register;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RoundLoadBalance implements LoadBalance {
    private int index = 0;

    @Override
    public String balance(List<String> addressList) {
        index++;
        log.info(index + "=="+ addressList.get(index % (addressList.size())));
        return addressList.get(index % (addressList.size()));
    }
}
