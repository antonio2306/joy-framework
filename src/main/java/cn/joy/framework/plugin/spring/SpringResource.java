package cn.joy.framework.plugin.spring;

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

}
