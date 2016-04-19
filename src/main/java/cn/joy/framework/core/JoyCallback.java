package cn.joy.framework.core;

/**
 * 通用回调接口
 * @author liyy
 * @date 2014-07-05
 */
public interface JoyCallback {
	public Object run(Object... params) throws Exception;
}
