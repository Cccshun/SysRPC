package serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import common.constants.SerializerType;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.K;
import protocol.SysMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Slf4j
public class KryoSerializer implements Serializer {

    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(true); //默认值就是 true，添加此行的目的是为了提醒维护者，不要改变这个配置
        kryo.setRegistrationRequired(false); //默认值就是 false，添加此行的目的是为了提醒维护者，不要改变这个配置
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); Output output = new Output(bos)) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeClassAndObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SysMessage deserialize(byte[] bytes, int msgType) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); Input input = new Input(bis)) {
            Kryo kryo = kryoThreadLocal.get();
            kryoThreadLocal.remove();
            return (SysMessage) kryo.readClassAndObject(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getSerializerType() {
        return SerializerType.KRYOSERIALIZER;
    }
}
