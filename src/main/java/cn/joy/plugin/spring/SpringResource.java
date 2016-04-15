package cn.joy.plugin.spring;

import java.util.HashMap;
import java.util.Map;

import cn.joy.plugin.spring.db.DbException;
import cn.joy.plugin.spring.db.RuleDao;
import cn.joy.plugin.spring.db.SpringDb;
/**
 * Spring资源注入
 * @author liyy
 * @date 2014-07-06
 */
public class SpringResource {
	private static RuleDao ruleDao;
	private static SpringDb mainDb;
	
	private static Map<String, SpringDb> dbMap = new HashMap();
	
	public static SpringDb getDb(String dbName) {
		SpringDb db = dbMap.get(dbName);
		if(db==null)
			throw new DbException("No DB with name "+dbName);
		return db;
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

}
