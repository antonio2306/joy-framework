package cn.joy.plugin.hibernate3;

import java.util.HashMap;
import java.util.Map;

import cn.joy.plugin.hibernate3.db.DbException;
import cn.joy.plugin.hibernate3.db.RuleDao;
import cn.joy.plugin.hibernate3.db.DbResource;
/**
 * DB资源注入
 * @author liyy
 * @date 2014-07-06
 */
public class ResourceInject {
	private static RuleDao ruleDao;
	private static DbResource mainDb;
	
	private static Map<String, DbResource> dbMap = new HashMap<>();
	
	public static DbResource getDb(String dbName) {
		DbResource db = dbMap.get(dbName);
		if(db==null)
			throw new DbException("No DB with name "+dbName);
		return db;
	}

	public static DbResource getMainDb() {
		return mainDb;
	}

	public void setMainDb(DbResource mainDb) {
		ResourceInject.mainDb = mainDb;
	}

	public static RuleDao getRuleDao() {
		return ruleDao;
	}

	public void setRuleDao(RuleDao ruleDao) {
		ResourceInject.ruleDao = ruleDao;
	}

}
