package cn.joy.framework.support;

import javax.servlet.http.HttpServletRequest;

import cn.joy.framework.rule.RuleResult;

public abstract class AppAuthManager{
	protected AppAuthInfoStore appAuthInfoStore;

	public AppAuthInfoStore getAppAuthInfoStore(){
		if(appAuthInfoStore==null){
			appAuthInfoStore = new DefaultAppAuthInfoStore();
		}
		return appAuthInfoStore;
	}

	public void setAppAuthInfoStore(AppAuthInfoStore appAuthInfoStore){
		this.appAuthInfoStore = appAuthInfoStore;
	}
	
	public abstract RuleResult checkAPIRequest(HttpServletRequest request);

	public abstract void signAPIResult(HttpServletRequest request, RuleResult result);
}
