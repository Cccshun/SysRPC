package test;

import com.google.gson.*;
import common.pojo.Person;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

public class Test {
    public static void main(String[] args) {
        zkTest();
    }

    public static void zkTest() {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .namespace("sys")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(40000)
                .build();
        client.start();
        System.out.println(client);
        try {
//            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/hehe");
            Stat stat = client.checkExists().forPath("/hehe");
            System.out.println(stat);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void readObject(String path, Gson gson) throws RuntimeException {
        try {
            FileInputStream fis = new FileInputStream(path);
            byte[] bytes = fis.readAllBytes();
            Person person = gson.fromJson(new String(bytes), Person.class);
            System.out.println(person);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object writeObject(String path, Gson gson) {
        Person person = new Person("zhangsan", 20, 18, 3);
        String json = gson.toJson(person);
        try {
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(json.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    static class ClassCodex implements JsonSerializer<Class>, JsonDeserializer<Class> {
        @Override
        public Class deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String asString = json.getAsString();
            try {
                return Class.forName(asString);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public JsonElement serialize(Class src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getName());
        }
    }
}
