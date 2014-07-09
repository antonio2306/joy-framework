package cn.joy.framework.plugin.spring;

import cn.joy.framework.plugin.spring.db.RuleDao;
import cn.joy.framework.plugin.spring.db.SpringDb;
import cn.joy.framework.plugin.spring.support.RouteStore;
import cn.joy.framework.support.SecurityManager;

public class SpringResource {
	private static RuleDao ruleDao;
	private static SpringDb mainDb;
	private static SecurityManager securityManager;
	private static RouteStore routeStore;

	public static SecurityManager getSecurityManager() {
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
		return routeStore;
	}

	public void setRouteStore(RouteStore routeStore) {
		SpringResource.routeStore = routeStore;
	}

}
