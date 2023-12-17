package stub;

import common.constants.SerializerType;
import protocol.Request;
import protocol.Response;
import io.NettyClient;
import io.RPCClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
@Slf4j
public class ClientStub implements InvocationHandler {
    private byte serialization;
    private RPCClient rpcClient;

    public ClientStub() {
        this.rpcClient = new NettyClient();
        this.serialization = SerializerType.JDKSERIALIZER;
    }

    public ClientStub(byte serialization) {
        this.rpcClient = new NettyClient();
        this.serialization = serialization;
    }

    public ClientStub(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
        this.serialization = SerializerType.JDKSERIALIZER;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 封装调用方法和参数
        Request request = Request.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes()).build();

        // 发送请求并接收返回结果
        Response response = rpcClient.sendRequest(request, serialization);
        return response.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }

    public void shutdown() {
        rpcClient.stop();
    }
}
