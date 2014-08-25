package cn.joy.framework.plugin.spring.db;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import cn.joy.framework.core.JoyThreadLocalMap;
/**
 * 数据访问接口，独立事务
 * @author liyy
 * @date 2014-05-20
 */
public class RuleDao extends HibernateDaoSupport {
	private Logger logger = Logger.getLogger(RuleDao.class);
	
	private JoyThreadLocalMap threadMap = new JoyThreadLocalMap();
	
	public boolean save(Object obj) {
		boolean result = true;
		try {
			getHibernateTemplate().save(obj);
		} catch (Exception e) {
			logger.error("add object error", e);
			result = false;
		}
		return result;
	}

	public boolean update(Object obj) {
		boolean result = true;
		try {
			getHibernateTemplate().update(obj);
		} catch (Exception e) {
			logger.error("update object error", e);
			result = false;
		}
		return result;
	}

	public boolean update(String entityName, Object obj) {
		boolean result = true;
		try {
			getHibernateTemplate().update(entityName, obj);
		} catch (Exception e) {
			logger.error("update object error", e);
			result = false;
		}
		return result;
	}

	public boolean save(String entityName, Object obj) {
		boolean result = true;
		try {
			getHibernateTemplate().save(entityName, obj);
		} catch (Exception e) {
			logger.error("add object error", e);
			result = false;
		}
		return result;
	}

	public boolean delete(Class clazz, Serializable s) {
		boolean result = true;
		try {
			result = delete(get(clazz, s));
		} catch (Exception e) {
			logger.error("delete object error", e);
			result = false;
		}
		return result;
	}
	
	public List list(String hql, Object[] params) {
		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql);
		for (int i = 0; params != null && i < params.length; i++)
			query.setParameter(i, params[i]);
		return query.list();
	}

	public List list(String hql) {
		return list(hql, null);
	}

	public List list(String hql, int start, int count, Object[] params) {
		List list = null;
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql);
			if (start >= 0) {
				query.setFirstResult(start);
			}
			if (count >= 0) {
				query.setMaxResults(count);
			}
			for (int i = 0; params != null && i < params.length; i++) {
				query.setParameter(i, params[i]);
			}
			list = query.list();
		} catch (Exception e) {
			logger.error("list error....", e);
		}
		return list;
	}

	public boolean delete(Object obj) {
		boolean result = true;
		if (obj == null) {
			result = false;
			return result;
		}
		try {
			getHibernateTemplate().delete(obj);
		} catch (Exception e) {
			logger.error("delete object error", e);
			result = false;
		}
		return result;
	}

	public Object get(Class clazz, Serializable s) {
		Object obj = null;
		try {
			logger.debug("try to get obj of " + clazz.getName() + " id=" + s);
			obj = getHibernateTemplate().get(clazz, s);
		} catch (Exception e) {
			logger.error("get object error", e);
		}
		return obj;
	}

	public int update(String hql) {
		return getHibernateTemplate().bulkUpdate(hql);
	}
	
	public int update(String hql, Object[] params) {
		return getHibernateTemplate().bulkUpdate(hql, params);
	}
	
}
