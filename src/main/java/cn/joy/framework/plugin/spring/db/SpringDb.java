package cn.joy.framework.plugin.spring.db;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import cn.joy.framework.exception.RuleException;
/**
 * 基于Spring、Hibernate的数据库定义
 * @author liyy
 * @date 2014-07-06
 */
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
			logger.debug("beginTransaction...");
		Session session = getSession();
		if (session != null){
			session.beginTransaction();
			threadLocal.set(session);
			if (logger.isDebugEnabled())
				logger.debug("beginTransaction do.");
		}
	}

	public void endTransaction() {
		if (logger.isDebugEnabled())
			logger.debug("endTransaction...");
		Session session = getThreadLocalSession();
		if (session != null && session.isOpen()){
			session.close();
			if (logger.isDebugEnabled())
				logger.debug("endTransaction do.");
		}
		threadLocal.remove();
	}

	public void commitAndEndTransaction() {
		Session session = getThreadLocalSession();
		if (session != null) {
			try {
				session.getTransaction().commit();
				if (logger.isDebugEnabled())
					logger.debug("commitAndEndTransaction do.");
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
		Session session = getThreadLocalSession();
		if (session != null) {
			try {
				session.getTransaction().rollback();
				if (logger.isDebugEnabled())
					logger.debug("rollbackAndEndTransaction do.");
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
