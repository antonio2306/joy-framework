package cn.joy.framework.plugin.spring.db;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import cn.joy.framework.exception.RuleException;

public class SpringDb {
	private Logger logger = Logger.getLogger(SpringDb.class);
	private SessionFactory sessionFactory = null;
	private final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public Session getThreadLocalSession() {
		return threadLocal.get();
	}
	
	public Session getSession() {
		Session session = getThreadLocalSession();
		if(session!=null)
			return session;
		return sessionFactory.openSession();
	}
	
	void beginTransaction(Session session) {
		if(getThreadLocalSession()==null)
			if (session != null) 
				session.beginTransaction();
	}

	void endTransaction(Session session) {
		if(getThreadLocalSession()==null)
			if (session != null && session.isOpen())
				session.close();
	}

	void commitAndEndTransaction(Session session) {
		if(getThreadLocalSession()==null){
			if (session != null) {
				try {
					session.getTransaction().commit();
				} catch (Exception e) {
					logger.error("", e);
					session.getTransaction().rollback();
					throw new RuleException(e);
				} finally {
					session.close();
				}
			}
		}
	}

	void rollbackAndEndTransaction(Session session) {
		if(getThreadLocalSession()==null){
			if (session != null) {
				try {
					session.getTransaction().rollback();
				} catch (Exception e) {
					logger.error("", e);
				} finally {
					session.close();
				}
			}
		}
	}

	void rollback(Session session) {
		if(getThreadLocalSession()==null)
			if (session != null)
				session.getTransaction().rollback();
	}

	public void beginTransaction() {
		if (logger.isDebugEnabled())
			logger.debug("begin session...");
		Session session = getSession();
		if (session != null) 
			session.beginTransaction();
			threadLocal.set(session);
	}

	public void endTransaction() {
		if (logger.isDebugEnabled())
			logger.debug("close session...");
		Session session = getThreadLocalSession();
		if (session != null && session.isOpen())
			session.close();
		threadLocal.remove();
	}

	public void commitAndEndTransaction() {
		if (logger.isDebugEnabled())
			logger.debug("commitAndCloseSession...");
		Session session = getThreadLocalSession();
		if (session != null) {
			try {
				session.getTransaction().commit();
			} catch (Exception e) {
				logger.error("", e);
				session.getTransaction().rollback();
				throw new RuleException(e);
			} finally {
				session.close();
				threadLocal.remove();
			}
		}
	}

	public void rollbackAndEndTransaction() {
		if (logger.isDebugEnabled())
			logger.debug("rollbackAndCloseSession...");
		Session session = getThreadLocalSession();
		if (session != null) {
			try {
				session.getTransaction().rollback();
			} catch (Exception e) {
				logger.error("", e);
			} finally {
				session.close();
				threadLocal.remove();
			}
		}
	}

	public void rollback() {
		Session session = getThreadLocalSession();
		if (session != null)
			session.getTransaction().rollback();
	}

	/* Hibernate4 移除了session.connection()方法
	public Connection getThreadLocalConnection() {
		Connection con = null;
		Session session = getThreadLocalSession();
		if (session != null)
			con = session.connection();
			// ((SessionFactoryImplementor)session.getSessionFactory()).getConnectionProvider().getConnection();
			// SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection()
		return con;
	}*/

}
