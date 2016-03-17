package cn.joy.framework.plugin.spring.db;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.spring.SpringResource;
/**
 * 数据库操作工具类
 * @author liyy
 * @date 2014-07-06
 */
public class Db {
	private static Logger logger = Logger.getLogger(Db.class);
	
	private static SpringDb use(){
		return use(null);
	}
	
	public static SpringDb use(String dbName){
		if(StringKit.isEmpty(dbName))
			return SpringResource.getMainDb();
		return SpringResource.getDb(dbName);
	}
	
	public static Session getSession() {
		return use().getSession();
	}
	
	public static boolean beginTransaction() {
		try {
			return use().beginTransaction();
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public static boolean beginNewTransaction() {
		try {
			return use().beginNewTransaction();
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public static boolean beginEmptyTransaction() {
		try {
			return use().beginEmptyTransaction();
		} catch (NullPointerException e) {
			return false;
		}
	}

	public static void endTransaction() {
		try {
			use().endTransaction();
		} catch (NullPointerException e) {
		}
	}

	public static void commitAndEndTransaction() {
		try {
			use().commitAndEndTransaction();
		} catch (NullPointerException e) {
		}
	}

	public static void rollbackAndEndTransaction() {
		try {
			use().rollbackAndEndTransaction();
		} catch (NullPointerException e) {
		}
	}
	
	public static List<Object[]> query(String sql, Object... params){
		return use().query(sql, params);
	}

	public static Object get(Class<?> clazz, Serializable pk) {
		return use().get(clazz, pk);
	}

	public static <T> T get(String hql, Object... params) {
		return use().get(hql, params);
	}
	
	public static <T> T get(String hql, Map<String, Object> namedParams, Object... params) {
		return use().get(hql, namedParams, params);
	}

	public static Object unique(String hql, Object... params) {
		return use().unique(hql, params);
	}
	
	public static <T> List<T> list(String hql, Object... params) {
		return use().list(hql, params);
	}
	
	public static <T> List<T> list(String hql, Map<String, Object> namedParams, Object... params) {
		return use().list(hql, namedParams, params);
	}

	public static <T> List<T> page(String hql, int start, int count, Object... params) {
		return use().page(hql, start, count, params);
	}
	
	public static <T> List<T> page(String hql, int start, int count, Map<String, Object> namedParams, Object... params) {
		return use().page(hql, start, count, namedParams, params);
	}
	
	public static List<Map> listMap(String hql, Object... params) {
		return use().listMap(hql, params);
	}
	
	public static List<Map> listMap(String hql, Map<String, Object> namedParams, Object... params) {
		return use().listMap(hql, namedParams, params);
	}
	
	public static List<Map> pageMap(String hql, int start, int count, Object... params) {
		return use().pageMap(hql, start, count, params);
	}
	
	public static List<Map> pageMap(String hql, int start, int count, Map<String, Object> namedParams, Object... params) {
		return use().pageMap(hql, start, count, namedParams, params);
	}

	public static Integer count(String hql, Object... params) {
		return use().count(hql, params);
	}

	public static int executeUpdate(String hql, Object... params) {
		return use().executeUpdate(hql, params);
	}
	
	public static int executeUpdate(String hql, Map<String, Object> namedParams, Object... params) {
		return use().executeUpdate(hql, namedParams, params);
	}

	public static void execute(DbCallback callback, Object... params) {
		use().execute(callback, params);
	}
	
	public static void add(Object obj) {
		use().add(obj);
	}

	public static <T> void addAll(List<T> list) {
		use().addAll(list);
	}

	public static void update(Object obj) {
		use().update(obj);
	}
	
	public static <T> void updateAll(List<T> list) {
		use().updateAll(list);
	}

	public static void saveOrUpdate(Object obj) {
		use().saveOrUpdate(obj);
	}

	public static void merge(Object obj) {
		use().merge(obj);
	}

	public static void delete(Object obj) {
		use().delete(obj);
	}
	
	public static <T> void deleteAll(List<T> list) {
		use().deleteAll(list);
	}
}
