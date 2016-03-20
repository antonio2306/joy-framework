package cn.joy.framework.plugin.spring.db;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
/**
 * 基于Spring、Hibernate的数据库定义
 * @author liyy
 * @date 2014-07-06
 */
public class SpringDb {
	private Logger logger = Logger.getLogger(SpringDb.class);
	private SessionFactory sessionFactory = null;
	private final ThreadLocal<LinkedList<Session>> threadLocal = new ThreadLocal<LinkedList<Session>>();
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private LinkedList<Session> getThreadLocal(){
		LinkedList<Session> sessionStack = threadLocal.get();
		if(sessionStack==null){
			sessionStack = new LinkedList<Session>();
			threadLocal.set(sessionStack);
		}
		return sessionStack;
	}
	
	Session getThreadLocalSession() {
		return getThreadLocal().peekLast();
	}
	
	//在当前线程中获取事务，如果为空，则开启自己的事务
	public Session getSession() {
		Session session = getThreadLocalSession();
		if(session!=null)
			return session;
		if(logger.isDebugEnabled())
			logger.debug("open single session...");
		return sessionFactory.openSession();
	}
	
	void beginTransaction(Session session) {
		if(session!=null && session!=getThreadLocalSession())
			session.beginTransaction();
	}

	void endTransaction(Session session) {
		if(session!=null && session!=getThreadLocalSession()){
			if (session.isOpen()){
				if(logger.isDebugEnabled())
					logger.debug("close single session...");
				session.close();
			}
		}
	}

	void commitAndEndTransaction(Session session) {
		if(session!=null && session!=getThreadLocalSession()){
			try {
				session.getTransaction().commit();
			} catch (Exception e) {
				logger.error("", e);
				session.getTransaction().rollback();
				throw new DbException(e);
			} finally {
				if(logger.isDebugEnabled())
					logger.debug("close single session...");
				session.close();
			}
		}
	}

	void rollbackAndEndTransaction(Session session) {
		if(session!=null && session!=getThreadLocalSession()){
			try {
				session.getTransaction().rollback();
			} catch (Exception e) {
				logger.error("", e);
				throw new DbException(e);
			} finally {
				if(logger.isDebugEnabled())
					logger.debug("close single session...");
				session.close();
			}
		}
	}
	
	void addLastSession(Session session) {
		if(logger.isDebugEnabled())
			logger.debug("add last session...");
		getThreadLocal().add(session);
	}
	
	Session removeLastSession() {
		if(logger.isDebugEnabled())
			logger.debug("close last session...");
		return getThreadLocal().pollLast();
	}

	public boolean beginTransaction() {
		Session session = getThreadLocalSession();
		if (session == null || !session.isOpen()){
			session = sessionFactory.openSession();
			session.beginTransaction();
			addLastSession(session);
			if (logger.isDebugEnabled())
				logger.debug("beginTransaction do.");
			return true;
		}
		return false;
	}

	public boolean beginNewTransaction() {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		addLastSession(session);
		if (logger.isDebugEnabled())
			logger.debug("beginNewTransaction do.");
		return true;
	}

	public boolean beginEmptyTransaction() {
		if (logger.isDebugEnabled())
			logger.debug("beginEmptyTransaction do.");
		addLastSession(null);
		return true;
	}
	
	public void endTransaction() {
		Session session = removeLastSession();
		if (logger.isDebugEnabled())
			logger.debug("endTransaction session="+session);
		if (session != null && session.isOpen()){
			session.close();
			if (logger.isDebugEnabled())
				logger.debug("endTransaction do.");
		}
	}

	public void commitAndEndTransaction() {
		Session session = removeLastSession();
		if (session != null) {
			try {
				session.getTransaction().commit();
				if (logger.isDebugEnabled())
					logger.debug("commitAndEndTransaction do.");
			} catch (Exception e) {
				logger.error("", e);
				session.getTransaction().rollback();
				throw new DbException(e);
			} finally {
				session.close();
			}
		}
	}

	public void rollbackAndEndTransaction() {
		Session session = removeLastSession();
		if (session != null) {
			try {
				session.getTransaction().rollback();
				if (logger.isDebugEnabled())
					logger.debug("rollbackAndEndTransaction do.");
			} catch (Exception e) {
				logger.error("", e);
				throw new DbException(e);
			} finally {
				session.close();
			}
		}
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
	
	public List<Object[]> query(String sql, Object... params) {
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			StringBuilder paramsInfo = null;
			if (logger.isDebugEnabled())
				paramsInfo = new StringBuilder();
			if (params != null) {
				for (int i = 0; i < params.length; i++){
					if (logger.isDebugEnabled())
						paramsInfo.append(params[i]).append(",");
					query.setParameter(i, params[i]);
				}
			}
			
			List<Object[]> list = query.list();
			if(list==null)
				list = new ArrayList<Object[]>();
			if (logger.isDebugEnabled())
				logger.debug("db list ==> " + sql+", params=["+paramsInfo+"], size="+list.size());
			return list;
		} catch (Exception e) {
			throw new DbException(e);
		} finally{
			endTransaction(session);
		}
	}

	public Object get(Class<?> clazz, Serializable pk) {
		if (logger.isDebugEnabled())
			logger.debug("db get ==> class="+clazz.getSimpleName()+", pk=" + pk);
		Session session = null;
		try {
			session = getSession();
			return session.get(clazz, pk);
		} catch (Exception e) {
			throw new DbException(e);
		} finally{
			endTransaction(session);
		}
	}

	public <T> T get(String hql, Object... params) {
		if (logger.isDebugEnabled())
			logger.debug("db get ==> " + hql);
		List<T> list = list(hql, params);
		if (list != null && list.size() > 0)
			return list.get(0);
		return null;
	}
	
	public <T> T get(String hql, Map<String, Object> namedParams, Object... params) {
		if (logger.isDebugEnabled())
			logger.debug("db get ==> " + hql);
		List<T> list = list(hql, namedParams, params);
		if (list != null && list.size() > 0)
			return list.get(0);
		return null;
	}

	public Object unique(String hql, Object... params) {
		Session session = null;
		try {
			session = getSession();
			Query query = session.createQuery(hql);
			StringBuilder paramsInfo = null;
			if (logger.isDebugEnabled())
				paramsInfo = new StringBuilder();
			if (params != null) {
				for (int i = 0; i < params.length; i++){
					if (logger.isDebugEnabled())
						paramsInfo.append(params[i]).append(",");
					query.setParameter(i, params[i]);
				}
			}
			
			if (logger.isDebugEnabled())
				logger.debug("db unique ==> " + hql+", params=["+paramsInfo+"]");
			return query.uniqueResult();
		} catch (Exception e) {
			throw new DbException(e);
		} finally{
			endTransaction(session);
		}
	}

	public <T> List<T> list(String hql, Object... params) {
		return list(hql, null, params);
	}
	
	public <T> List<T> list(String hql, Map<String, Object> namedParams, Object... params) {
		Session session = null;
		try {
			session = getSession();
			Query query = session.createQuery(hql);
			StringBuilder paramsInfo = null;
			if (logger.isDebugEnabled())
				paramsInfo = new StringBuilder();
			if (params != null) {
				for (int i = 0; i < params.length; i++){
					if (logger.isDebugEnabled())
						paramsInfo.append(params[i]).append(",");
					query.setParameter(i, params[i]);
				}
			}
			
			if (namedParams != null) {
				for (Entry<String, Object> entry:namedParams.entrySet()){
					String name = entry.getKey();
					Object value = entry.getValue();
					if (logger.isDebugEnabled())
						logger.debug("set named param["+name+"] = "+value);
					if(value instanceof Collection)
						query.setParameterList(name, (Collection)value);
					else if(value instanceof Object[])
						query.setParameterList(name, (Object[])value);
					else
						query.setParameter(name, value);
				}
			}
			
			List<T> list = query.list();
			if(list==null)
				list = new ArrayList<T>();
			if (logger.isDebugEnabled())
				logger.debug("db list ==> " + hql+", params=["+paramsInfo+"], size="+list.size());
			return list;
		} catch (Exception e) {
			throw new DbException(e);
		} finally{
			endTransaction(session);
		}
	}
	
	public List<Map> listMap(String hql, Object... params) {
		return listMap(hql, null, params);
	}
	
	public List<Map> listMap(String hql, Map<String, Object> namedParams, Object... params) {
		Session session = null;
		try {
			session = getSession();
			Query query = session.createQuery(hql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			StringBuilder paramsInfo = null;
			if (logger.isDebugEnabled())
				paramsInfo = new StringBuilder();
			if (params != null) {
				for (int i = 0; i < params.length; i++){
					if (logger.isDebugEnabled())
						paramsInfo.append(params[i]).append(",");
					query.setParameter(i, params[i]);
				}
			}
			
			if (namedParams != null) {
				for (Entry<String, Object> entry:namedParams.entrySet()){
					String name = entry.getKey();
					Object value = entry.getValue();
					if (logger.isDebugEnabled())
						logger.debug("set named param["+name+"] = "+value);
					if(value instanceof Collection)
						query.setParameterList(name, (Collection)value);
					else if(value instanceof Object[])
						query.setParameterList(name, (Object[])value);
					else
						query.setParameter(name, value);
				}
			}
			
			List<Map> list = query.list();
			if(list==null)
				list = new ArrayList<Map>();
			if (logger.isDebugEnabled())
				logger.debug("db listMap ==> " + hql+", params=["+paramsInfo+"], size="+list.size());
			return list;
		} catch (Exception e) {
			throw new DbException(e);
		} finally{
			endTransaction(session);
		}
	}

	public <T> List<T> page(String hql, int start, int count, Object... params) {
		return page(hql, start, count, null, params);
	}
	
	public <T> List<T> page(String hql, int start, int count, Map<String, Object> namedParams, Object... params) {
		Session session = null;
		try {
			session = getSession();
			Query query = session.createQuery(hql);
			StringBuilder paramsInfo = null;
			if (logger.isDebugEnabled())
				paramsInfo = new StringBuilder();
			if (params != null) {
				for (int i = 0; i < params.length; i++){
					if (logger.isDebugEnabled())
						paramsInfo.append(params[i]).append(",");
					query.setParameter(i, params[i]);
				}
			}
			
			if (namedParams != null) {
				for (Entry<String, Object> entry:namedParams.entrySet()){
					String name = entry.getKey();
					Object value = entry.getValue();
					if (logger.isDebugEnabled())
						logger.debug("set named param["+name+"] = "+value);
					if(value instanceof Collection)
						query.setParameterList(name, (Collection)value);
					else if(value instanceof Object[])
						query.setParameterList(name, (Object[])value);
					else
						query.setParameter(name, value);
				}
			}
			
			if (start < 0)
				start = 0;
			if (count <= 0)
				count = 10;
			
			List<T> list = query.setFirstResult(start).setMaxResults(count).list();
			if(list==null)
				list = new ArrayList<T>();
			if (logger.isDebugEnabled())
				logger.debug("db page ==> " + hql+", params=["+paramsInfo+"], start="+start+", count="+count+", size="+list.size());
			return list;
		} catch (Exception e) {
			throw new DbException(e);
		} finally{
			endTransaction(session);
		}
	}
	
	public List<Map> pageMap(String hql, int start, int count, Object... params) {
		return pageMap(hql, start, count, null, params);
	}
	
	public List<Map> pageMap(String hql, int start, int count, Map<String, Object> namedParams, Object... params) {
		Session session = null;
		try {
			session = getSession();
			Query query = session.createQuery(hql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			StringBuilder paramsInfo = null;
			if (logger.isDebugEnabled())
				paramsInfo = new StringBuilder();
			if (params != null) {
				for (int i = 0; i < params.length; i++){
					if (logger.isDebugEnabled())
						paramsInfo.append(params[i]).append(",");
					query.setParameter(i, params[i]);
				}
			}
			
			if (namedParams != null) {
				for (Entry<String, Object> entry:namedParams.entrySet()){
					String name = entry.getKey();
					Object value = entry.getValue();
					if (logger.isDebugEnabled())
						logger.debug("set named param["+name+"] = "+value);
					if(value instanceof Collection)
						query.setParameterList(name, (Collection)value);
					else if(value instanceof Object[])
						query.setParameterList(name, (Object[])value);
					else
						query.setParameter(name, value);
				}
			}
			
			if (start < 0)
				start = 0;
			if (count <= 0)
				count = 10;
			
			List<Map> list = query.setFirstResult(start).setMaxResults(count).list();
			if(list==null)
				list = new ArrayList<Map>();
			if (logger.isDebugEnabled())
				logger.debug("db pageMap ==> " + hql+", params=["+paramsInfo+"], start="+start+", count="+count+", size="+list.size());
			return list;
		} catch (Exception e) {
			throw new DbException(e);
		} finally{
			endTransaction(session);
		}
	}

	public Integer count(String hql, Object... params) {
		if (logger.isDebugEnabled())
			logger.debug("db count ==> " + hql);
		Number count = (Number) unique(hql, params);
		if (count == null)
			return 0;
		return count.intValue();
	}
	
	public int executeUpdate(String hql, Object... positionParams) {
		return executeUpdate(hql, null, positionParams);
	}

	public int executeUpdate(String hql, Map<String, Object> namedParams, Object... positionParams) {
		int result = 0;
		Session session = getSession();
		try {
			beginTransaction(session);
			Query query = session.createQuery(hql);
			StringBuilder paramsInfo = null;
			if (logger.isDebugEnabled())
				paramsInfo = new StringBuilder();
			if (positionParams != null) {
				for (int i = 0; i < positionParams.length; i++){
					if (logger.isDebugEnabled())
						paramsInfo.append(positionParams[i]).append(",");
					query.setParameter(i, positionParams[i]);
				}
			}
			if (logger.isDebugEnabled())
				logger.debug("db executeUpdate ==> " + hql+", params=["+paramsInfo+"]");
			
			if (namedParams != null) {
				for (Entry<String, Object> entry:namedParams.entrySet()){
					String name = entry.getKey();
					Object value = entry.getValue();
					if (logger.isDebugEnabled())
						logger.debug("set named param["+name+"] = "+value);
					if(value instanceof Collection)
						query.setParameterList(name, (Collection)value);
					else if(value instanceof Object[])
						query.setParameterList(name, (Object[])value);
					else
						query.setParameter(name, value);
				}
			}
			
			result = query.executeUpdate();
			commitAndEndTransaction(session);
		} catch (Exception e) {
			rollbackAndEndTransaction(session);
			throw new DbException(e);
		} finally {
			endTransaction(session);
		}
		return result;
	}

	public void execute(DbCallback callback, Object... params) {
		//if (logger.isDebugEnabled())
		//	logger.debug("db execute ==> callback");
		Session session = getSession();
		try {
			beginTransaction(session);
			callback.run(session, params);
			commitAndEndTransaction(session);
		} catch (Exception e) {
			rollbackAndEndTransaction(session);
			throw new DbException(e);
		} finally {
			endTransaction(session);
		}
	}
	
	private void setLastModifyTime(Object obj){
		if( obj != null ){
    		try{
    			Class<?> clazz = obj.getClass();
        		Class<?>[] paramTypes = {Date.class};
        		Method method = clazz.getMethod("setLastmodifytime", paramTypes);
        		Object[] objs = {new Date()};
        		method.invoke( obj, objs );
			}catch (Exception e){
			}    		
    	}
	}

	public void add(Object obj) {
		if (logger.isDebugEnabled())
			logger.debug("db add ==> " + obj);

		execute(new DbCallback() {
			public void run(Session session, Object... params) throws Exception {
				setLastModifyTime(params[0]);
				session.save(params[0]);
			}
		}, obj);
	}

	public <T> void addAll(List<T> list) {
		if (logger.isDebugEnabled())
			logger.debug("db addAll ==> " + list);

		execute(new DbCallback() {
			public void run(Session session, Object... params) throws Exception {
				List<T> list = (List<T>) params[0];
				if (list != null) {
					session.setCacheMode(CacheMode.IGNORE);
					for (int i = 0; i < list.size(); i++) {
						T obj = list.get(i);
						setLastModifyTime(obj);
						session.save(obj);
						if (i % 50 == 0) {
							session.flush();
							session.clear();
						}
					}
					session.setCacheMode(CacheMode.NORMAL);
				}
			}
		}, list);
	}

	public void update(Object obj) {
		if (logger.isDebugEnabled())
			logger.debug("db update ==> " + obj);
		execute(new DbCallback() {
			public void run(Session session, Object... params) throws Exception {
				setLastModifyTime(params[0]);
				session.update(params[0]);
			}
		}, obj);
	}
	
	public <T> void updateAll(List<T> list) {
		if (logger.isDebugEnabled())
			logger.debug("db updateAll ==> " + list);

		execute(new DbCallback() {
			public void run(Session session, Object... params) throws Exception {
				List<T> list = (List<T>) params[0];
				if (list != null) {
					if (list != null) {
						for (T obj:list) {
							setLastModifyTime(obj);
							session.update(obj);
						}
					}
				}
			}
		}, list);
	}

	public void saveOrUpdate(Object obj) {
		if (logger.isDebugEnabled())
			logger.debug("db saveOrUpdate ==> " + obj);
		execute(new DbCallback() {
			public void run(Session session, Object... params) throws Exception {
				setLastModifyTime(params[0]);
				session.saveOrUpdate(params[0]);
			}
		}, obj);
	}

	public void merge(Object obj) {
		if (logger.isDebugEnabled())
			logger.debug("db merge ==> " + obj);
		execute(new DbCallback() {
			public void run(Session session, Object... params) throws Exception {
				setLastModifyTime(params[0]);
				session.merge(params[0]);
			}
		}, obj);
	}

	public void delete(Object obj) {
		if (logger.isDebugEnabled())
			logger.debug("db delete ==> " + obj);
		execute(new DbCallback() {
			public void run(Session session, Object... params) throws Exception {
				session.delete(params[0]);
			}
		}, obj);
	}
	
	public <T> void deleteAll(List<T> list) {
		if (logger.isDebugEnabled())
			logger.debug("db deleteAll ==> " + list);

		execute(new DbCallback() {
			public void run(Session session, Object... params) throws Exception {
				List<T> list = (List<T>) params[0];
				if (list != null) {
					for (T obj:list) {
						session.delete(obj);
					}
				}
			}
		}, list);
	}
}
