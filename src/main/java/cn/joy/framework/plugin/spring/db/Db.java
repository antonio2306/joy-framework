package cn.joy.framework.plugin.spring.db;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.Session;

import cn.joy.framework.exception.RuleException;
import cn.joy.framework.plugin.spring.SpringResource;

public class Db {
	private static Logger logger = Logger.getLogger(Db.class);
	private static SpringDb mainDb = SpringResource.getMainDb();

	public static Session getSession() {
		return mainDb.getSession();
	}
	
	public static void beginTransaction() {
		mainDb.beginTransaction();
	}

	public static void endTransaction() {
		mainDb.endTransaction();
	}

	public static void commitAndEndTransaction() {
		mainDb.commitAndEndTransaction();
	}

	public static void rollbackAndEndTransaction() {
		mainDb.rollbackAndEndTransaction();
	}

	public static Object get(Class<?> clazz, Serializable pk) {
		if (logger.isDebugEnabled())
			logger.debug("db get ==> " + pk);
		Session session = mainDb.getSession();
		return session.get(clazz, pk);
	}

	public static <T> T get(String hql, Object... params) {
		if (logger.isDebugEnabled())
			logger.debug("db get ==> " + hql);
		List<T> list = list(hql, params);
		if (list != null && list.size() > 0)
			return list.get(0);
		return null;
	}

	public static Object unique(String hql, Object... params) {
		if (logger.isDebugEnabled())
			logger.debug("db unique ==> " + hql);
		Session session = mainDb.getSession();
		Query query = session.createQuery(hql);
		if (params != null) {
			for (int i = 0; i < params.length; i++)
				query.setParameter(i, params[i]);
		}
		return query.uniqueResult();
	}

	public static <T> List<T> list(String hql, Object... params) {
		if (logger.isDebugEnabled())
			logger.debug("db list ==> " + hql);
		Session session = mainDb.getSession();
		Query query = session.createQuery(hql);
		if (params != null) {
			for (int i = 0; i < params.length; i++)
				query.setParameter(i, params[i]);
		}

		List<T> list = query.list();
		return list == null ? new ArrayList<T>() : list;
	}

	public static <T> List<T> page(String hql, int start, int max, Object... params) {
		if (logger.isDebugEnabled())
			logger.debug("db page ==> " + hql);
		Session session = mainDb.getSession();
		Query query = session.createQuery(hql);
		if (params != null) {
			for (int i = 0; i < params.length; i++)
				query.setParameter(i, params[i]);
		}
		if (start < 0)
			start = 0;
		if (max < 0)
			max = 10;
		List<T> list = query.setFirstResult(start).setMaxResults(max).list();
		return list == null ? new ArrayList<T>() : list;
	}

	public static Integer count(String hql, Object... params) {
		if (logger.isDebugEnabled())
			logger.debug("db count ==> " + hql);
		Number count = (Number) unique(hql, params);
		if (count == null)
			return 0;
		return count.intValue();
	}

	public static int executeUpdate(String hql, Object... params) {
		if (logger.isDebugEnabled())
			logger.debug("db executeUpdate ==> " + hql);
		int result = 0;
		Session session = mainDb.getSession();
		try {
			mainDb.beginTransaction(session);
			Query query = session.createQuery(hql);
			result = query.executeUpdate();
			mainDb.commitAndEndTransaction(session);
		} catch (Exception e) {
			mainDb.rollbackAndEndTransaction(session);
		} finally {
			mainDb.endTransaction(session);
		}
		return result;
	}

	public static void execute(DbCallback callback, Object... params) {
		if (logger.isDebugEnabled())
			logger.debug("db execute ==> " + callback);
		Session session = mainDb.getSession();
		try {
			mainDb.beginTransaction(session);
			callback.run(session, params);
			mainDb.commitAndEndTransaction(session);
		} catch (Exception e) {
			mainDb.rollbackAndEndTransaction(session);
		} finally {
			mainDb.endTransaction(session);
		}
	}
	
	private static void setLastModifyTime(Object obj){
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

	public static void add(Object obj) {
		if (logger.isDebugEnabled())
			logger.debug("db add ==> " + obj);

		execute(new DbCallback() {
			public void run(Session session, Object... params) throws Exception {
				setLastModifyTime(params[0]);
				session.save(params[0]);
			}
		}, obj);
	}

	public static <T> void addAll(List<T> list) {
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

	public static void update(Object obj) {
		if (logger.isDebugEnabled())
			logger.debug("db update ==> " + obj);
		execute(new DbCallback() {
			public void run(Session session, Object... params) throws Exception {
				setLastModifyTime(params[0]);
				session.update(params[0]);
			}
		}, obj);
	}
	
	public static <T> void updateAll(List<T> list) {
		if (logger.isDebugEnabled())
			logger.debug("db updateAll ==> " + list);

		execute(new DbCallback() {
			public void run(Session session, Object... params) throws Exception {
				List<T> list = (List<T>) params[0];
				if (list != null) {
					if (list != null) {
						for (T obj:list) {
							session.update(obj);
						}
					}
				}
			}
		}, list);
	}

	public static void saveOrUpdate(Object obj) {
		if (logger.isDebugEnabled())
			logger.debug("db saveOrUpdate ==> " + obj);
		execute(new DbCallback() {
			public void run(Session session, Object... params) throws Exception {
				session.saveOrUpdate(params[0]);
			}
		}, obj);
	}

	public static void merge(Object obj) {
		if (logger.isDebugEnabled())
			logger.debug("db merge ==> " + obj);
		execute(new DbCallback() {
			public void run(Session session, Object... params) throws Exception {
				session.merge(params[0]);
			}
		}, obj);
	}

	public static void delete(Object obj) {
		if (logger.isDebugEnabled())
			logger.debug("db delete ==> " + obj);
		execute(new DbCallback() {
			public void run(Session session, Object... params) throws Exception {
				session.delete(params[0]);
			}
		}, obj);
	}
	
	public static <T> void deleteAll(List<T> list) {
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
