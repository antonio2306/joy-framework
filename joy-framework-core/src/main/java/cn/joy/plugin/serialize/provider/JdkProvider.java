package cn.joy.plugin.serialize.provider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cn.joy.framework.provider.SerializeProvider;

@SuppressWarnings("unchecked")
public class JdkProvider extends SerializeProvider {

	/**
	 * 序列化（对象 -> 字节数组）
	 */
	public <T> byte[] serialize(T obj) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);) {
			oos.writeObject(obj);
			return baos.toByteArray();
		} catch (Exception e) {
			log.error("", e);
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * 反序列化（字节数组 -> 对象）
	 */
	public <T> T deserialize(byte[] data, Class<T> cls) {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
				ObjectInputStream ois = new ObjectInputStream(bais);
		) {
			return (T)ois.readObject();
		} catch (Exception e) {
			log.error("", e);
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

}
