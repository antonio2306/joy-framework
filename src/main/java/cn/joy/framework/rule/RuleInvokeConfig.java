package cn.joy.framework.rule;

import cn.joy.framework.core.JoyBundle;

/**
 * 业务规则执行配置
 * @author liyy
 * @date 2014-06-16
 */
public class RuleInvokeConfig extends JoyBundle<RuleInvokeConfig> {
	public static final RuleInvokeConfig NULL_RULE_INVOKE_CONFIG = RuleInvokeConfig.create();
	
	private String invokeWay = "http";
	private String httpMethod = "post";
	private boolean isAsyn = false;
	private boolean isChangePerson = false;
	private boolean isChangeCompany = false;
	private String loginId;
	private String companyCode;
	
	public static RuleInvokeConfig create(){
		return new RuleInvokeConfig();
	}
	
	public RuleInvokeConfig setAsyn(boolean isAsyn){
		this.isAsyn = isAsyn;
		return this;
	}
	
	public boolean isAsyn(){
		return isAsyn;
	}
	
	public boolean isChangePerson(){
		return isChangePerson;
	}
	
	public boolean isChangeCompany(){
		return isChangeCompany;
	}
	/**
	 * 更换为个人身份
	 */
	public RuleInvokeConfig change(String loginId){
		this.isChangePerson = true;
		this.loginId = loginId;
		return this;
	}
	/**
	 * 更换个人及其组织身份
	 */
	public RuleInvokeConfig change(String loginId, String companyCode){
		this.isChangePerson = true;
		this.isChangeCompany = true;
		this.loginId = loginId;
		this.companyCode = companyCode;
		return this;
	}
	/**
	 * 只更换组织身份
	 */
	public RuleInvokeConfig changeCompany(String companyCode){
		this.isChangeCompany = true;
		this.companyCode = companyCode;
		return this;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
}
