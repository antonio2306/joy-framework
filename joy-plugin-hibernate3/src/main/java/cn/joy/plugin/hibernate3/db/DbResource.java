package cn.joy.plugin.hibernate3.db;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
/**
 * 基于Spring、Hibernate的数据库定义
 * @author liyy
 * @date 2014-07-06
 */
public class DbResource {
	private Log logger = LogKit.get();
	private SessionFactory sessionFactory = null;
	private final ThreadLocal<LinkedList<DbSession>> threadLocal = new ThreadLocal<LinkedList<DbSession>>();
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private LinkedList<DbSession> getThreadLocal(){
		LinkedList<DbSession> sessionStack = threadLocal.get();
		if(sessionStack==null){
			sessionStack = new LinkedList<DbSession>();
			threadLocal.set(sessionStack);
		}
		return sessionStack;
	}
	
	DbSession getThreadLocalSession() {
		return getThreadLocal().peekLast();
	}
	
	//在当前线程中获取事务，如果为空，则开启自己的事务
	public DbSession getSession() {
		DbSession dbSession = getThreadLocalSession();
		if(dbSession!=null)
			return dbSession;
		logger.debug("open single session...");
		return new DbSession(sessionFactory.openSession());
	}
	
	public void addTransactionCallback(JoyCallback transactionCallback){
		DbSession dbSession = getThreadLocalSession();
		if(dbSession!=null){
			logger.debug("add transaction callback...");
			dbSession.setTransactionCallback(transactionCallback);
		}
	}
	
	public void removeTransactionCallback(){
		DbSession dbSession = getThreadLocalSession();
		if(dbSession!=null){
			logger.debug("remove transaction callback...");
			dbSession.setTransactionCallback(null);
		}
	}
	
	void beginTransaction(DbSession dbSession) {
		if(dbSession!=null && dbSession!=getThreadLocalSession())
			dbSession.beginTransaction();
	}

	void endTransaction(DbSession dbSession) {
		if(dbSession!=null && dbSession!=getThreadLocalSession()){
			if (dbSession.isOpen()){
				logger.debug("close single session...");
				dbSession.close();
			}
		}
	}

	void commitAndEndTransaction(DbSession dbSession) {
		if(dbSession!=null && dbSession!=getThreadLocalSession()){
			try {
				dbSession.commit();
			} catch (Exception e) {
				logger.error("", e);
				dbSession.rollback();
				throw new DbException(e);
			} finally {
				logger.debug("close single session...");
				dbSession.close();
			}
		}
	}

	void rollbackAndEndTransaction(DbSession dbSession) {
		if(dbSession!=null && dbSession!=getThreadLocalSession()){
			try {
				dbSession.rollback();
			} catch (Exception e) {
				logger.error("", e);
				throw new DbException(e);
			} finally {
				logger.debug("close single session...");
				dbSession.close();
			}
		}
	}
	
	void addLastSession(DbSession dbSession) {
		logger.debug("add last session...");
		getThreadLocal().add(dbSession);
	}
	
	DbSession removeLastSession() {
		logger.debug("close last session...");
		return getThreadLocal().pollLast();
	}

	public boolean beginTransaction() {
		DbSession dbSession = getThreadLocalSession();
		if (dbSession == null || !dbSession.isOpen()){
			dbSession = new DbSession(sessionFactory.openSession());
			dbSession.beginTransaction();
			addLastSession(dbSession);
			logger.debug("beginTransaction do.");
			return true;
		}
		return false;
	}

	public boolean beginNewTransaction() {
		DbSession dbSession = new DbSession(sessionFactory.openSession());
		dbSession.beginTransaction();
		addLastSession(dbSession);
		logger.debug("beginNewTransaction do.");
		return true;
	}

	public boolean beginEmptyTransaction() {
		logger.debug("beginEmptyTransaction do.");
		addLastSession(null);
		return true;
	}
	
	public void endTransaction() {
		DbSession dbSession = removeLastSession();
		logger.debug("endTransaction session");
		if (dbSession != null && dbSession.isOpen()){
			dbSession.close();
			logger.debug("endTransaction do.");
		}
	}

	public void commitAndEndTransaction() {
		DbSession dbSession = removeLastSession();
		if (dbSession != null) {
			try {
				dbSession.commit();
				logger.debug("commitAndEndTransaction do.");
			} catch (Exception e) {
				logger.error("", e);
				dbSession.rollback();
				throw new DbException(e);
			} finally {
				dbSession.close();
			}
		}
	}

	public void rollbackAndEndTransaction() {
		DbSession dbSession = removeLastSession();
		if (dbSession != null) {
			try {
				dbSession.rollback();
				logger.debug("rollbackAndEndTransaction do.");
			} catch (Exception e) {
				logger.error("", e);
				throw new DbException(e);
			} finally {
				dbSession.close();
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
		DbSession dbSession = null;
		try {
			dbSession = getSession();
			SQLQuery query = dbSession.getSession().createSQLQuery(sql);
			StringBuilder paramsInfo = null;
			if (logger.getLogger().isDebugEnabled())
				paramsInfo = new StringBuilder();
			if (params != null) {
				for (int i = 0; i < params.length; i++){
					if (logger.getLogger().isDebugEnabled())
						paramsInfo.append(params[i]).append(",");
					query.setParameter(i, params[i]);
				}
			}
			
			List<Object[]> list = query.list();
			if(list==null)
				list = new ArrayList<Object[]>();
			if (logger.getLogger().isDebugEnabled())
				logger.debug("db list ==> " + sql+", params=["+paramsInfo+"], size="+list.size());
			return list;
		} catch (Exception e) {
			throw new DbException(e);
		} finally{
			endTransaction(dbSession);
		}
	}

	public Object get(Class<?> clazz, Serializable pk) {
		logger.debug("db get ==> class={}, pk={}", clazz.getSimpleName(), pk);
		DbSession dbSession = null;
		try {
			dbSession = getSession();
			return dbSession.getSession().get(clazz, pk);
		} catch (Exception e) {
			throw new DbException(e);
		} finally{
			endTransaction(dbSession);
		}
	}

	public <T> T get(String hql, Object... params) {
		logger.debug("db get ==> " + hql);
		List<T> list = list(hql, params);
		if (list != null && list.size() > 0)
			return list.get(0);
		return null;
	}
	
	public <T> T get(String hql, Map<String, Object> namedParams, Object... params) {
		logger.debug("db get ==> " + hql);
		List<T> list = list(hql, namedParams, params);
		if (list != null && list.size() > 0)
			return list.get(0);
		return null;
	}

	public Object unique(String hql, Object... params) {
		DbSession dbSession = null;
		try {
			dbSession = getSession();
			Query query = dbSession.getSession().createQuery(hql);
			StringBuilder paramsInfo = null;
			if (logger.getLogger().isDebugEnabled())
				paramsInfo = new StringBuilder();
			if (params != null) {
				for (int i = 0; i < params.length; i++){
					if (logger.getLogger().isDebugEnabled())
						paramsInfo.append(params[i]).append(",");
					query.setParameter(i, params[i]);
				}
			}
			
			if (logger.getLogger().isDebugEnabled())
				logger.debug("db unique ==> " + hql+", params=["+paramsInfo+"]");
			return query.uniqueResult();
		} catch (Exception e) {
			throw new DbException(e);
		} finally{
			endTransaction(dbSession);
		}
	}

	public <T> List<T> list(String hql, Object... params) {
		return list(hql, null, params);
	}
	
	public <T> List<T> list(String hql, Map<String, Object> namedParams, Object... params) {
		DbSession dbSession = null;
		try {
			dbSession = getSession();
			Query query = dbSession.getSession().createQuery(hql);
			StringBuilder paramsInfo = null;
			if (logger.getLogger().isDebugEnabled())
				paramsInfo = new StringBuilder();
			if (params != null) {
				for (int i = 0; i < params.length; i++){
					if (logger.getLogger().isDebugEnabled())
						paramsInfo.append(params[i]).append(",");
					query.setParameter(i, params[i]);
				}
			}
			
			if (namedParams != null) {
				for (Entry<String, Object> entry:namedParams.entrySet()){
					String name = entry.getKey();
					Object value = entry.getValue();
					if (logger.getLogger().isDebugEnabled())
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
			if (logger.getLogger().isDebugEnabled())
				logger.debug("db list ==> " + hql+", params=["+paramsInfo+"], size="+list.size());
			return list;
		} catch (Exception e) {
			throw new DbException(e);
		} finally{
			endTransaction(dbSession);
		}
	}
	
	public List<Map> listMap(String hql, Object... params) {
		return listMap(hql, null, params);
	}
	
	public List<Map> listMap(String hql, Map<String, Object> namedParams, Object... params) {
		DbSession dbSession = null;
		try {
			dbSession = getSession();
			Query query = dbSession.getSession().createQuery(hql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			StringBuilder paramsInfo = null;
			if (logger.getLogger().isDebugEnabled())
				paramsInfo = new StringBuilder();
			if (params != null) {
				for (int i = 0; i < params.length; i++){
					if (logger.getLogger().isDebugEnabled())
						paramsInfo.append(params[i]).append(",");
					query.setParameter(i, params[i]);
				}
			}
			
			if (namedParams != null) {
				for (Entry<String, Object> entry:namedParams.entrySet()){
					String name = entry.getKey();
					Object value = entry.getValue();
					if (logger.getLogger().isDebugEnabled())
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
			if (logger.getLogger().isDebugEnabled())
				logger.debug("db listMap ==> " + hql+", params=["+paramsInfo+"], size="+list.size());
			return list;
		} catch (Exception e) {
			throw new DbException(e);
		} finally{
			endTransaction(dbSession);
		}
	}

	public <T> List<T> page(String hql, int start, int count, Object... params) {
		return page(hql, start, count, null, params);
	}
	
	public <T> List<T> page(String hql, int start, int count, Map<String, Object> namedParams, Object... params) {
		DbSession dbSession = null;
		try {
			dbSession = getSession();
			Query query = dbSession.getSession().createQuery(hql);
			StringBuilder paramsInfo = null;
			if (logger.getLogger().isDebugEnabled())
				paramsInfo = new StringBuilder();
			if (params != null) {
				for (int i = 0; i < params.length; i++){
					if (logger.getLogger().isDebugEnabled())
						paramsInfo.append(params[i]).append(",");
					query.setParameter(i, params[i]);
				}
			}
			
			if (namedParams != null) {
				for (Entry<String, Object> entry:namedParams.entrySet()){
					String name = entry.getKey();
					Object value = entry.getValue();
					if (logger.getLogger().isDebugEnabled())
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
			if (logger.getLogger().isDebugEnabled())
				logger.debug("db page ==> " + hql+", params=["+paramsInfo+"], start="+start+", count="+count+", size="+list.size());
			return list;
		} catch (Exception e) {
			throw new DbException(e);
		} finally{
			endTransaction(dbSession);
		}
	}
	
	public List<Map> pageMap(String hql, int start, int count, Object... params) {
		return pageMap(hql, start, count, null, params);
	}
	
	public List<Map> pageMap(String hql, int start, int count, Map<String, Object> namedParams, Object... params) {
		DbSession dbSession = null;
		try {
			dbSession = getSession();
			Query query = dbSession.getSession().createQuery(hql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			StringBuilder paramsInfo = null;
			if (logger.getLogger().isDebugEnabled())
				paramsInfo = new StringBuilder();
			if (params != null) {
				for (int i = 0; i < params.length; i++){
					if (logger.getLogger().isDebugEnabled())
						paramsInfo.append(params[i]).append(",");
					query.setParameter(i, params[i]);
				}
			}
			
			if (namedParams != null) {
				for (Entry<String, Object> entry:namedParams.entrySet()){
					String name = entry.getKey();
					Object value = entry.getValue();
					if (logger.getLogger().isDebugEnabled())
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
			if (logger.getLogger().isDebugEnabled())
				logger.debug("db pageMap ==> " + hql+", params=["+paramsInfo+"], start="+start+", count="+count+", size="+list.size());
			return list;
		} catch (Exception e) {
			throw new DbException(e);
		} finally{
			endTransaction(dbSession);
		}
	}

	public Integer count(String hql, Object... params) {
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
		DbSession dbSession = getSession();
		try {
			beginTransaction(dbSession);
			Query query = dbSession.getSession().createQuery(hql);
			StringBuilder paramsInfo = null;
			if (logger.getLogger().isDebugEnabled())
				paramsInfo = new StringBuilder();
			if (positionParams != null) {
				for (int i = 0; i < positionParams.length; i++){
					if (logger.getLogger().isDebugEnabled())
						paramsInfo.append(positionParams[i]).append(",");
					query.setParameter(i, positionParams[i]);
				}
			}
			if (logger.getLogger().isDebugEnabled())
				logger.debug("db executeUpdate ==> " + hql+", params=["+paramsInfo+"]");
			
			if (namedParams != null) {
				for (Entry<String, Object> entry:namedParams.entrySet()){
					String name = entry.getKey();
					Object value = entry.getValue();
					if (logger.getLogger().isDebugEnabled())
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
			commitAndEndTransaction(dbSession);
		} catch (Exception e) {
			rollbackAndEndTransaction(dbSession);
			throw new DbException(e);
		} finally {
			endTransaction(dbSession);
		}
		return result;
	}

	public void execute(DbCallback callback, Object... params) {
		//if (logger.isDebugEnabled())
		//	logger.debug("db execute ==> callback");
		DbSession dbSession = getSession();
		try {
			beginTransaction(dbSession);
			callback.run(dbSession, params);
			commitAndEndTransaction(dbSession);
		} catch (Exception e) {
			rollbackAndEndTransaction(dbSession);
			throw new DbException(e);
		} finally {
			endTransaction(dbSession);
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
		logger.debug("db add ==> " + obj);

		execute(new DbCallback() {
			public void run(DbSession dbSession, Object... params) throws Exception {
				setLastModifyTime(params[0]);
				dbSession.getSession().save(params[0]);
			}
		}, obj);
	}

	public <T> void addAll(List<T> list) {
		logger.debug("db addAll ==> " + list);

		execute(new DbCallback() {
			public void run(DbSession dbSession, Object... params) throws Exception {
				List<T> list = (List<T>) params[0];
				if (list != null) {
					Session session = dbSession.getSession();
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
		logger.debug("db update ==> " + obj);
		execute(new DbCallback() {
			public void run(DbSession dbSession, Object... params) throws Exception {
				setLastModifyTime(params[0]);
				dbSession.getSession().update(params[0]);
			}
		}, obj);
	}
	
	public <T> void updateAll(List<T> list) {
		logger.debug("db updateAll ==> " + list);

		execute(new DbCallback() {
			public void run(DbSession dbSession, Object... params) throws Exception {
				List<T> list = (List<T>) params[0];
				if (list != null) {
					Session session = dbSession.getSession();
					for (T obj:list) {
						setLastModifyTime(obj);
						session.update(obj);
					}
				}
			}
		}, list);
	}

	public void saveOrUpdate(Object obj) {
		logger.debug("db saveOrUpdate ==> " + obj);
		execute(new DbCallback() {
			public void run(DbSession dbSession, Object... params) throws Exception {
				setLastModifyTime(params[0]);
				dbSession.getSession().saveOrUpdate(params[0]);
			}
		}, obj);
	}

	public void merge(Object obj) {
		logger.debug("db merge ==> " + obj);
		execute(new DbCallback() {
			public void run(DbSession dbSession, Object... params) throws Exception {
				setLastModifyTime(params[0]);
				dbSession.getSession().merge(params[0]);
			}
		}, obj);
	}

	public void delete(Object obj) {
		logger.debug("db delete ==> " + obj);
		execute(new DbCallback() {
			public void run(DbSession dbSession, Object... params) throws Exception {
				dbSession.getSession().delete(params[0]);
			}
		}, obj);
	}
	
	public <T> void deleteAll(List<T> list) {
		logger.debug("db deleteAll ==> " + list);

		execute(new DbCallback() {
			public void run(DbSession dbSession, Object... params) throws Exception {
				List<T> list = (List<T>) params[0];
				if (list != null) {
					Session session = dbSession.getSession();
					for (T obj:list) {
						session.delete(obj);
					}
				}
			}
		}, list);
	}
}
