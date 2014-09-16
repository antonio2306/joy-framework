package cn.joy.framework.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.BeanKit;
import cn.joy.framework.kits.StringKit;
/**
 * 业务规则加载器
 * @author liyy
 * @date 2014-05-20
 */
public class RuleLoader {
	private Logger logger = Logger.getLogger(RuleLoader.class);
	
	private Map<String, BaseRule> rules = new HashMap<String, BaseRule>();
	
	private Set<String> debugRuleSet = new HashSet<String>();
	
	private static RuleLoader rLoader;
	
	private RuleLoader(){
		
	}
	
	public static RuleLoader singleton(){
		if(rLoader==null)
			rLoader = new RuleLoader();
		return rLoader;
	}
	
	public void startDebug(String ruleURI){
		debugRuleSet.add(getFullRuleURI(ruleURI));
	}
	
	public void stopDebug(String ruleURI){
		debugRuleSet.remove(getFullRuleURI(ruleURI));
	}
	
	public List<String> listDebugRules(){
		List<String> list = new ArrayList<String>();
		list.addAll(debugRuleSet);
		Collections.sort(list);
		return list;
	}

	public BaseRule loadRule(String ruleURI) {
		String fullRuleURI = getFullRuleURI(ruleURI);
		
		BaseRule rule = null;
		
		if (debugRuleSet.contains(fullRuleURI)) {
            logger.debug("load debug rule...");

            ClassLoader parent = Thread.currentThread().getContextClassLoader();
            logger.debug("parent classloader=" + parent);
            RuleDebugClassLoader rdcl = new RuleDebugClassLoader(parent);
            try {
				Class ruleClass = rdcl.loadRuleClass(fullRuleURI);
				return (BaseRule) ruleClass.newInstance();
			} catch (Exception e) {
				logger.error("", e);
			}
        } else{
        	rule = rules.get(fullRuleURI);
    		if (rule == null) {
    			try {
    				rule = (BaseRule) BeanKit.getNewInstance(fullRuleURI);
    				rules.put(fullRuleURI, rule);
    			} catch (Exception e) {
    				logger.error("", e);
    			}
    		}
        }
		
		return rule;
	}
	
	private String getFullRuleURI(String ruleURI) {
		int idx = ruleURI.indexOf("#");
		if(idx>0)
			ruleURI = ruleURI.substring(0, idx);
		if(ruleURI.startsWith(JoyManager.getServer().getModulePackage()))
			return ruleURI;
		
		String moduleName = "";
		String ruleName = "";
		if (ruleURI.indexOf(".") == -1){
			moduleName = JoyManager.getServer().getDefaultModule();
			ruleName = ruleURI;
		}else{
			String[] ruleURIInfo = ruleURI.split("\\.");
			moduleName = ruleURIInfo[0];
			ruleName = ruleURIInfo[1];
		}
		
		String fullRuleURI = String.format(JoyManager.getServer().getRuleURIPattern(), moduleName, StringKit.capitalize(ruleName));
		if(logger.isDebugEnabled())
			logger.debug("ruleURI="+ruleURI+", fullRuleURI="+fullRuleURI);
		return fullRuleURI;
	}
	
}
