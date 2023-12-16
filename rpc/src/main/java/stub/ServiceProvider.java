package stub;


import register.Register;
import register.ZkRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServiceProvider {
    private final Map<String, Object> serviceProvider;
    private final Register register;
    private final String host;
    private final int port;

    public ServiceProvider(String host, int port) {
        serviceProvider = new HashMap<>();
        register = new ZkRegister();
        this.host = host;
        this.port = port;
    }

    public void providerServiceInterface(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class<?> clazz : interfaces) {
            serviceProvider.put(clazz.getName(), service);
            register.serviceRegister(clazz.getName(), new InetSocketAddress(host, port));
        }
    }

    public Object getService(String interfaceName) {
        return serviceProvider.get(interfaceName);
    }
}
