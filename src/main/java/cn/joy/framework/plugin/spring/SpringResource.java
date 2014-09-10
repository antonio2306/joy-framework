package cn.joy.framework.plugin.spring;

import java.util.HashMap;
import java.util.Map;

import cn.joy.framework.plugin.spring.db.DbException;
import cn.joy.framework.plugin.spring.db.RuleDao;
import cn.joy.framework.plugin.spring.db.SpringDb;
import cn.joy.framework.plugin.spring.support.DefaultRouteStore;
import cn.joy.framework.plugin.spring.support.DefaultSecurityManager;
import cn.joy.framework.plugin.spring.support.RouteStore;
import cn.joy.framework.support.SecurityManager;
/**
 * Spring资源注入
 * @author liyy
 * @date 2014-07-06
 */
public class SpringResource {
	private static RuleDao ruleDao;
	private static SpringDb mainDb;
	private static SecurityManager securityManager;
	private static RouteStore routeStore;
	public static String MVC_OPEN_REQUEST_URL = "openservice.do";
	public static String MVC_BUSINESS_REQUEST_URL = "businessservice.do";
	
	private static Map<String, SpringDb> dbMap = new HashMap();
	
	public static SpringDb getDb(String dbName) {
		SpringDb db = dbMap.get(dbName);
		if(db==null)
			throw new DbException("No DB with name "+dbName);
		return db;
	}

	public static SecurityManager getSecurityManager() {
		if(securityManager==null)
			securityManager = new DefaultSecurityManager();
		return securityManager;
	}

	public void setSecurityManager(SecurityManager securityManager) {
		SpringResource.securityManager = securityManager;
	}

	public static SpringDb getMainDb() {
		return mainDb;
	}

	public void setMainDb(SpringDb mainDb) {
		SpringResource.mainDb = mainDb;
	}

	public static RuleDao getRuleDao() {
		return ruleDao;
	}

	public void setRuleDao(RuleDao ruleDao) {
		SpringResource.ruleDao = ruleDao;
	}

	public static RouteStore getRouteStore() {
		if(routeStore==null)
			routeStore = new DefaultRouteStore();
		return routeStore;
	}

	public void setRouteStore(RouteStore routeStore) {
		SpringResource.routeStore = routeStore;
	}

	public void setMVC_OPEN_REQUEST_URL(String mVC_OPEN_REQUEST_URL) {
		SpringResource.MVC_OPEN_REQUEST_URL = mVC_OPEN_REQUEST_URL;
	}

	public void setMVC_BUSINESS_REQUEST_URL(String mVC_BUSINESS_REQUEST_URL) {
		SpringResource.MVC_BUSINESS_REQUEST_URL = mVC_BUSINESS_REQUEST_URL;
	}

}
