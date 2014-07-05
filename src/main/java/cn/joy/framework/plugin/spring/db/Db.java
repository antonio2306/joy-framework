package cn.joy.framework.plugin.spring.db;

import org.apache.log4j.Logger;
import org.hibernate.Session;

public class Db {
	private Logger logger = Logger.getLogger(Db.class);
	
	/*public static boolean beginSession() {
		Session session = getSessionFromThreadLocal();
		if (session == null || !session.isOpen()) {
			session = sessionFactory.openSession();
			session.beginTransaction();
			setSessionToThreadLocal(session);
			// 在每次会话开始前默认是提交到数据库的
			setNeedCommit(true);
			return true;
		} else {
			return false;
		}
	}

	public boolean closeSession() {
		log.debug("close session...");
		Session session = getSessionFromThreadLocal();
		if (session != null && session.isOpen()) {
			session.close();
		}
		removeSessionFromThreadLocal();
		return true;
	}

	private void rollback(Session session) {
		try {
			session.getTransaction().rollback();
		} catch (Exception ee) {
			log.error("rollback exception...");
		}
	}


	public boolean commitAndCloseSession() throws ActizHibernateException {
		log.debug("commitAndCloseSession...");
		Session session = getSessionFromThreadLocal();
		if (session != null) {
			try {
				if (isNeedCommit()) {
					session.getTransaction().commit();
				}
				session.close();
				removeSessionFromThreadLocal();
			} catch (Exception e) {
				log.error("dataset commit error...", e);
				rollback(session);
				session.close();
				removeSessionFromThreadLocal();
				throw new ActizHibernateException(e);
			}
		}
		return true;
	}

	
	public boolean rollback() {
		Session session = getSessionFromThreadLocal();
		if (session != null) {
			rollback(session);
		}
		return true;
	}

	
	public boolean rollbackAndCloseSession() {
		log.debug("rollbackAndCloseSession...");
		Session session = getSessionFromThreadLocal();
		if (session != null) {
			rollback(session);
			session.close();
			removeSessionFromThreadLocal();
		}
		return true;
	}
*/
	public static <T> T get(String hql) {
		return null;
	}
}
