package cn.joy.framework.provider;

import java.util.Properties;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;

public abstract class SerializeProvider implements JoyProvider {
	protected Log log = LogKit.getLog(SerializeProvider.class);
	public static SerializeProvider use(){
		return use(null);
	}
	
	public static SerializeProvider use(String way){
		return (SerializeProvider)JoyManager.provider(SerializeProvider.class, way);
	}
	
	@Override
	public void init(Properties prop) {
		// TODO Auto-generated method stub

	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

	public abstract <T> byte[] serialize(T obj);
	public abstract <T> T deserialize(byte[] data, Class<T> cls);

}
