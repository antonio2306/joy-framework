package cn.joy.framework.plugin.mvc.spring;

import cn.joy.framework.plugin.mvc.spring.db.RuleDao;

public class SpringResource{
	private static RuleDao ruleDao;
	
	public static RuleDao getrDao() {
		return ruleDao;
	}

	public void setRuleDao(RuleDao ruleDao) {
		SpringResource.ruleDao = ruleDao;
	}
	
}
