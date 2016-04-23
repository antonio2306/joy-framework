package cn.joy.plugin.hibernate3.db;

import org.hibernate.Session;

/**
 * 数据库操作通用回调接口
 * @author liyy
 * @date 2014-07-06
 */
public interface DbCallback {
	public void run(Session session, Object... params) throws Exception;
}
