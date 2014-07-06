package cn.joy.framework.plugin.spring;

import cn.joy.framework.plugin.spring.db.RuleDao;
import cn.joy.framework.plugin.spring.db.SpringDb;

public class SpringResource{
	private static RuleDao ruleDao;
	private static SpringDb mainDb;
	
	public static SpringDb getMainDb() {
		return mainDb;
	}

	public void setMainDb(SpringDb mainDb) {
		SpringResource.mainDb = mainDb;
	}

	public static RuleDao getrDao() {
		return ruleDao;
	}

	public void setRuleDao(RuleDao ruleDao) {
		SpringResource.ruleDao = ruleDao;
	}
	
}
