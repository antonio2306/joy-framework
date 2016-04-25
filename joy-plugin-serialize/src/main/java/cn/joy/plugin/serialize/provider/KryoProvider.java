package cn.joy.plugin.serialize.provider;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import cn.joy.framework.provider.SerializeProvider;

@SuppressWarnings("unchecked")
public class KryoProvider extends SerializeProvider {
	static Kryo kryo = new Kryo();

	/**
	 * 序列化（对象 -> 字节数组）
	 */
	public <T> byte[] serialize(T obj) {
		byte[] buffer = new byte[2048];
		try (Output output = new Output(buffer);) {
			kryo.writeClassAndObject(output, obj);
			return output.toBytes();
		} catch (Exception e) {
			log.error("", e);
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * 反序列化（字节数组 -> 对象）
	 */
	public <T> T deserialize(byte[] data, Class<T> cls) {
		try (Input input = new Input(data);) {
			return (T)kryo.readClassAndObject(input);
		} catch (Exception e) {
			log.error("", e);
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

}
