package cn.joy.framework.rule;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.exception.SubErrorType;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.RuleKit;
import cn.joy.framework.kits.StringKit;
/**
 * 业务规则执行上下文，记录当前操作者的身份
 * @author liyy
 * @date 2014-05-20
 */
public class RuleContext {
	private static Logger logger = Logger.getLogger(RuleContext.class);
	public static final String CLEAR = "CLEAR";
	public static final String NONE_LOGINID = "NONE";
	public static final String SYSTEM_LOGINID = "SYSTEM";
	public static final String LOGINID_IN_REQUEST = "LOGINID_IN_REQUEST";	 
	
	//用户主帐号，代表用户的个人身份
	private String loginId;
	//用户组织号，代表用户的组织身份
	private String companyCode;
	private String sceneKey;
	private String trimSceneKey;	//trim 'test'
	private String ip;
	private String sourceIP;
	private String appId;
	//非传递属性
	private String ruleURI;
	private HttpServletRequest request;
	
	private RuleContext(){
		
	}
	
	public static RuleContext create(){
		RuleContext rContext = new RuleContext();
		rContext.loginId = NONE_LOGINID;
		return rContext;
	}
	
	public static RuleContext create(HttpServletRequest request){
		if(request==null)
			return create();
		RuleContext rContext = new RuleContext();
		
		rContext.loginId = StringKit.getString(RuleKit.getStringParam(request, JoyManager.getServer().getLoginIdParam()),
				RuleKit.getStringAttribute(request, LOGINID_IN_REQUEST), NONE_LOGINID);
		//if(StringKit.isEmpty(rContext.loginId))
			//throw new RuleException(SubErrorType.ISV_MISSING_PARAMETER, "loginId");
		
		rContext.companyCode = RuleKit.getStringParam(request, JoyManager.getServer().getCompanyCodeParam());
		String sceneKey = RuleKit.getStringParam(request, JoyManager.getServer().getSceneKeyParam());
		if(StringKit.isEmpty(sceneKey))
			sceneKey = RuleKit.getStringAttribute(request, JoyManager.getServer().getSessionSceneKeyParam());
		rContext.sceneKey = StringKit.getString(sceneKey, JoyManager.getServer().getDefaultSceneKey());
		if(rContext.sceneKey.endsWith("test"))
			rContext.trimSceneKey = rContext.sceneKey.substring(0, rContext.sceneKey.length()-4);
		else
			rContext.trimSceneKey = rContext.sceneKey;
		rContext.ip = HttpKit.getClientIP(request); 
		//调用service rule时从当前context里获取sourceIP，如果没有则获取ip，然后作为sourceIP参数传递
		rContext.sourceIP = RuleKit.getStringParam(request, JoyManager.getServer().getSourceIPParam());
		rContext.appId = RuleKit.getStringParam(request, JoyManager.getServer().getAppIdParam());
		
		rContext.request = request;
		if(logger.isDebugEnabled())
			logger.debug("loginId="+rContext.loginId+", companyCode="+rContext.companyCode+", sceneKey="+rContext.sceneKey
					+", ip="+rContext.ip+", sourceIP="+rContext.sourceIP+", appId="+rContext.appId);
		return rContext;
	}
	
	/**
	 * 规则中调用其它规则时，可以通过config指定在克隆的上下文中新的当前操作者身份
	 */
	/*void configAs(RuleInvokeConfig config) {
		if(config!=null){
			if(config.isChangePerson()){
				String loginId = config.getLoginId();
				if(StringKit.isEmpty(loginId))
					throw new RuleException(SubErrorType.ISV_MISSING_PARAMETER, "loginId");
				this.loginId = StringKit.getString(loginId);
			}
			if(config.isChangeCompany()){
				this.companyCode = StringKit.getString(config.getCompanyCode());
			}
			if(logger.isDebugEnabled())
				logger.debug("configAs, loginId="+loginId+", companyCode="+companyCode);
		}
	}*/
	
	/**
	 * 规则中调用其它规则
	 */
	public RuleResult invokeRule(String ruleURI, RuleParam rParam) throws Exception{
		return invoke(ruleURI, rParam, null, null, false);
	}
	
	public RuleResult invokeRule(String ruleURI, RuleParam rParam, String user) throws Exception{
		return invoke(ruleURI, rParam, user, null, false);
	}
	
	public RuleResult invokeRule(String ruleURI, RuleParam rParam, String user, String company) throws Exception{
		return invoke(ruleURI, rParam, user, company, false);
	}
	
	public RuleResult invokeRuleAsyn(String ruleURI, RuleParam rParam) throws Exception{
		return invoke(ruleURI, rParam, null, null, true);
	}
	
	public RuleResult invokeRuleAsyn(String ruleURI, RuleParam rParam, String user) throws Exception{
		return invoke(ruleURI, rParam, user, null, true);
	}
	
	public RuleResult invokeRuleAsyn(String ruleURI, RuleParam rParam, String user, String company) throws Exception{
		return invoke(ruleURI, rParam, user, company, true);
	}
	
	private RuleResult invoke(String ruleURI, RuleParam rParam, String user, String company, boolean asyn) throws Exception{
		RuleContext cloneContext = this.cloneContext().uri(ruleURI);
		if(CLEAR.equals(user))
			cloneContext.loginId = NONE_LOGINID;
		else if(StringKit.isNotEmpty(user))
			cloneContext.loginId = user;
		
		if(CLEAR.equals(company))
			cloneContext.companyCode = null;
		else if(StringKit.isNotEmpty(company))
			cloneContext.companyCode = company;
		
		if(!asyn){
			RuleResult ruleResult = JoyManager.getRuleExecutor().execute(cloneContext, rParam);
			if(!ruleResult.isSuccess())
				throw new RuleException(ruleResult);
			return ruleResult;
		}else
			return JoyManager.getRuleExecutor().executeAsyn(cloneContext, rParam);
	}
	
	/**
	 * 规则中调用其它规则，支持config参数，用于指定新的操作者身份、是否异步调用等
	 */
	/*public RuleResult invokeRule(String ruleURI, RuleParam rParam, RuleInvokeConfig config) throws Exception{
		if(StringKit.isEmpty(ruleURI))
			return RuleResult.create().fail(SubError.createMain(SubErrorType.ISP_SERVICE_UNAVAILABLE, ruleURI));
		boolean isRemote = ruleURI.indexOf("@")>=0;
		boolean isAsyn = false;
		if(config!=null)
			isAsyn = config.isAsyn();
		
		if(isRemote){
			return RuleExecutor.createRemote(this.cloneContext(), config).execute(ruleURI, rParam);
		}else{
			if(isAsyn){
				return RuleExecutor.create(this.cloneContext(), config).execute(ruleURI, rParam);
			}else{
				if(config!=null)
					this.configAs(config);
				return this.getExecutor().executeInner(ruleURI, rParam);
			}
		}
	}*/
	
	private RuleContext cloneContext(){
		RuleContext context = new RuleContext();
		//复制需要保留的属性
		context.loginId = this.loginId;
		context.companyCode = this.companyCode;
		context.sceneKey = this.sceneKey;
		context.trimSceneKey = this.trimSceneKey;
		context.ip = this.ip;
		context.sourceIP = StringKit.getString(this.sourceIP, this.ip);
		context.appId = this.appId;
		if(logger.isDebugEnabled())
			logger.debug("cloneContext, loginId="+context.loginId+", companyCode="+context.companyCode+", sceneKey="+context.sceneKey
					+", ip="+context.ip+", sourceIP="+context.sourceIP+", appId="+context.appId);
		return context;
	}
	
	/**
	 * 远程HTTP调用时，将指定的操作者身份属性拼接为URL参数
	 */
	String prepareRemoteContextParam() {
		StringBuilder params = new StringBuilder();
		params.append("&"+JoyManager.getServer().getLoginIdParam()+"=").append(loginId);
		if(StringKit.isNotEmpty(companyCode))
			params.append("&"+JoyManager.getServer().getCompanyCodeParam()+"=").append(companyCode);
		if(StringKit.isNotEmpty(sceneKey))
			params.append("&"+JoyManager.getServer().getSceneKeyParam()+"=").append(sceneKey);
		if(StringKit.isNotEmpty(sourceIP))
			params.append("&"+JoyManager.getServer().getSourceIPParam()+"=").append(sourceIP);
		if(StringKit.isNotEmpty(appId))
			params.append("&"+JoyManager.getServer().getAppIdParam()+"=").append(appId);
		if(logger.isDebugEnabled())
			logger.debug("prepareRemoteContextParam, params="+params);
		return params.toString();
	}
	
	public void release(){
		this.request = null;
	}
	
	public RuleContext user(String loginId){
		if(StringKit.isNotEmpty(loginId))
			this.loginId = loginId;
		return this;
	}
	
	public RuleContext company(String companyCode){
		if(StringKit.isNotEmpty(companyCode))
			this.companyCode = companyCode;
		return this;
	}
	
	public RuleContext sceneKey(String sceneKey){
		if(StringKit.isNotEmpty(sceneKey))
			this.sceneKey = sceneKey;
		return this;
	}
	
	public RuleContext sourceIP(String sourceIP){
		this.sourceIP = sourceIP;
		return this;
	}
	
	public RuleContext appId(String appId){
		this.appId = appId;
		return this;
	}
	
	public RuleContext uri(String ruleURI){
		this.ruleURI = ruleURI;
		return this;
	}
	
	public String user() {
		return loginId;
	}
	
	public String company() {
		return companyCode;
	}
	
	public String sceneKey() {
		return sceneKey;
	}
	
	public String trimSceneKey() {
		return trimSceneKey;
	}
	
	@Deprecated
	public String getCompanyCode() {
		return companyCode;
	}
	
	@Deprecated
	public String getLoginId() {
		return loginId;
	}
	
	@Deprecated
	public String getSceneKey() {
		return sceneKey;
	}
	
	@Deprecated
	public String getTrimSceneKey() {
		return trimSceneKey;
	}

	public String ip(){
		return ip;
	}

	public String sourceIP(){
		return sourceIP;
	}
	
	public String appId(){
		return appId;
	}
	
	public HttpServletRequest request(){
		return request;
	}
	
	public String uri(){
		return ruleURI;
	}
}
