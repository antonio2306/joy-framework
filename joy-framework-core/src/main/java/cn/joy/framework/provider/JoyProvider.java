package cn.joy.framework.provider;

import java.util.Properties;

public interface JoyProvider {
	void init(Properties prop);
	void release();
}
