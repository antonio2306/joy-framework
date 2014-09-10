package cn.joy.framework.rule;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.exception.SubError;
import cn.joy.framework.exception.SubErrorType;
import cn.joy.framework.kits.RuleKit;
import cn.joy.framework.kits.StringKit;
/**
 * 业务规则执行上下文，记录当前操作者的身份
 * @author liyy
 * @date 2014-05-20
 */
public class RuleContext {
	private static Logger logger = Logger.getLogger(RuleContext.class);
	public static final String UNKNOWN_LOGINID = "UNKNOWN";
	public static final String NOLOGIN_LOGINID = "NO_LOGIN";
	public static final String SYSTEM_LOGINID = "SYSTEM";
	public static final String TRANSPORT_LOGINID = "TRANSPORT";
	public static final String TRANSPORT_SINGLE = "SINGLE";
	public static final String NONE_LOGINID = "NONE";	 //有时不需要用户信息，如组织的操作
	
	public static final String LOGINID_IN_REQUEST = "LOGINID_IN_REQUEST";	 
	
	private HttpServletRequest request;
	
	private RuleExecutor rExecutor;
	
	private String accessToken;
	
	//用户主帐号，代表用户的个人身份
	private String loginId;
	
	//用户组织号，代表用户的组织身份
	private String companyCode;
	
	private RuleExtraData rExtra;
	
	private RuleContext(){
		
	}
	
	public static RuleContext createSingle(String loginId){
		RuleContext rContext = new RuleContext();
		rContext.loginId = StringKit.getString(loginId, TRANSPORT_SINGLE);
		
		rContext.rExtra = RuleExtraData.create();
		if(logger.isDebugEnabled())
			logger.debug("loginId="+rContext.loginId);
		return rContext;
	}
	
	public static RuleContext create(HttpServletRequest request){
		RuleContext rContext = new RuleContext();
		rContext.request = request;

		rContext.accessToken = RuleKit.getStringParam(request, JoyManager.getServer().getAccessTokenParam());
		
		rContext.loginId = RuleKit.getStringParam(request, JoyManager.getServer().getLoginIdParam());
		if(StringKit.isEmpty(rContext.loginId))
			rContext.loginId = RuleKit.getStringAttribute(request, LOGINID_IN_REQUEST);
		if(StringKit.isEmpty(rContext.loginId))
			throw new RuleException(SubErrorType.ISV_MISSING_PARAMETER, "loginId");
		
		rContext.companyCode = RuleKit.getStringParam(request, JoyManager.getServer().getCompanyCodeParam());
		
		rContext.rExtra = RuleExtraData.create();
		if(logger.isDebugEnabled())
			logger.debug("loginId="+rContext.loginId+", companyCode="+rContext.companyCode);
		return rContext;
	}
	
	RuleContext(RuleContextParam params){
		//TODO
	}
	
	void bindExecutor(RuleExecutor rExecutor){
		this.rExecutor = rExecutor;
	}
	
	/**
	 * 规则中调用其它规则时，可以通过config指定在克隆的上下文中新的当前操作者身份
	 */
	void configAs(RuleInvokeConfig config) {
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
	}
	
	/**
	 * 远程HTTP调用时，将指定的操作者身份属性拼接为URL参数
	 */
	public String prepareRemoteContextParam() {
		StringBuilder params = new StringBuilder();
		params.append("&loginId=").append(loginId);
		if(StringKit.isNotEmpty(companyCode))
			params.append("&"+JoyManager.getServer().getCompanyCodeParam()+"=").append(companyCode);
		if(logger.isDebugEnabled())
			logger.debug("prepareRemoteContextParam, params="+params);
		return params.toString();
	}
	
	/**
	 * 规则中调用其它规则
	 */
	public RuleResult invokeRule(String ruleURI, RuleParam rParam) throws Exception{
		return this.invokeRule(ruleURI, rParam, RuleInvokeConfig.create().setAsyn(false));
	}
	
	/**
	 * 规则中调用其它规则，支持config参数，用于指定新的操作者身份、是否异步调用等
	 */
	public RuleResult invokeRule(String ruleURI, RuleParam rParam, RuleInvokeConfig config) throws Exception{
		if(StringKit.isEmpty(ruleURI))
			return RuleResult.create().fail(SubError.createMain(SubErrorType.ISP_SERVICE_UNAVAILABLE, ruleURI));
		boolean isRemote = ruleURI.indexOf("@")>0;
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
	}
	
	private RuleContext cloneContext(){
		RuleContext context = new RuleContext();
		//复制需要保留的属性
		context.loginId = this.loginId;
		context.companyCode = this.companyCode;
		return context;
	}
	
	public void clear(){
		this.request = null;
		this.rExecutor = null;
		this.loginId = null;
		this.companyCode = null;
		this.rExtra = null;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public RuleExecutor getExecutor() {
		return rExecutor;
	}

	public String getCompanyCode() {
		return companyCode;
	}
	
	public String getLoginId() {
		return loginId;
	}

	public RuleExtraData getExtra(){
		return rExtra;
	}
	
	public RuleContext clearExtra(){
		rExtra.clear();
		return this;
	}
	
	public RuleContext putData(String key, Object value){
		rExtra.put(key, value);
		return this;
	}
	
	public Object getData(String key){
		return this.getExtra().get(key);
	}

}
