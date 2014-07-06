package cn.joy.framework.plugin.spring.db;

import org.hibernate.Session;

public interface DbCallback {
	public void run(Session session, Object... params) throws Exception;
}
