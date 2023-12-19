package register;

import common.exception.RpcError;
import common.exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import balancer.LoadBalance;
import balancer.RoundLoadBalance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZkRegister implements Register {
    private final CuratorFramework client;
    private static final String ROOT_PATH = "Sys";
    private final LoadBalance loadBalancer;

    public ZkRegister() {
        client = CuratorFrameworkFactory.builder()
                .connectString("localhost")
                .connectionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace(ROOT_PATH)
                .build();
        client.start();
        loadBalancer = new RoundLoadBalance();
    }

    public ZkRegister(CuratorFramework curator) {
        client = curator;
        client.start();
        loadBalancer = new RoundLoadBalance();
    }

    public ZkRegister(CuratorFramework curator, LoadBalance loadBalancer) {
        client = curator;
        client.start();
        this.loadBalancer = loadBalancer;
    }

    @Override
    public void serviceRegister(String serviceName, InetSocketAddress socketAddress) {
        String servicePath = "/" + serviceName;
        try {
            // register persistent service
            if (client.checkExists().forPath(servicePath) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath);
            }
            // register ephemeral address of service provider
            String node = servicePath + "/" + getServiceAddress(socketAddress);
            client.create().withMode(CreateMode.EPHEMERAL).forPath(node);
        } catch (KeeperException.NodeExistsException e) {
            log.info(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        String servicePath = "/" + serviceName;
        try {
            List<String> nodeList = client.getChildren().forPath(servicePath);
            if (nodeList.isEmpty()) {
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            // get a service provider address
            String node = loadBalancer.balance(nodeList);
            return parseAddress(node);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getServiceAddress(InetSocketAddress socketAddress) {
        return socketAddress.getHostName() + ":" + socketAddress.getPort();
    }

    private InetSocketAddress parseAddress(String serviceAddress) {
        String[] split = serviceAddress.split(":");
        return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
    }
}
