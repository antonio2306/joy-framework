package cn.joy.framework.rule;

import javax.servlet.http.HttpServletRequest;
/**
 * 控制器类型规则基类
 * @author liyy
 * @date 2014-06-16
 */
public abstract class BaseControllerRule extends BaseTransactionRule{
	
	public Class[] getActionMethodParamClass(){
		return new Class[]{RuleContext.class, RuleParam.class, HttpServletRequest.class};
	}
	
	public Object[] getActionMethodParam(RuleContext rContext, RuleParam rParam) {
		return new Object[]{rContext, rParam, rContext.getRequest()};
	}
	
	public RuleResult execute(RuleContext rContext, RuleParam rParam, HttpServletRequest request) throws Exception{
		return RuleResult.empty().fail("EMPTY　IMPL");
	}
}
