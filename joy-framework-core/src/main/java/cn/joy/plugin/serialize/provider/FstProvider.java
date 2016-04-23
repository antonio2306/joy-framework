package cn.joy.plugin.serialize.provider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import cn.joy.framework.provider.SerializeProvider;

public class FstProvider extends SerializeProvider {
	
	@Override
	public <T> byte[] serialize(T obj) {
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		try(FSTObjectOutput fstOut = new FSTObjectOutput(bytesOut)) {
			fstOut.writeObject(obj);
			fstOut.flush();
			return bytesOut.toByteArray();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T deserialize(byte[] data, Class<T> cls) {
		if(data == null || data.length == 0)
			return null;
		
		try(FSTObjectInput fstInput = new FSTObjectInput(new ByteArrayInputStream(data))) {
			return (T)fstInput.readObject();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
}



