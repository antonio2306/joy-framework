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
 * 业务规则执行上下文
 * @author liyy
 * @date 2014-05-20
 */
public class RuleContext {
	private static Logger logger = Logger.getLogger(RuleContext.class);
	public static final Long UNKNOWN_COMPANYID = 0L;
	public static final String UNKNOWN_LOGINID = "UNKNOWN";
	public static final String NOLOGIN_LOGINID = "NO_LOGIN";
	public static final String SYSTEM_LOGINID = "SYSTEM";
	public static final String TRANSPORT_LOGINID = "TRANSPORT";
	public static final String TRANSPORT_SINGLE = "SINGLE";
	public static final String NONE_LOGINID = "NONE";	 //有时不需要用户信息，如组织的操作
	
	private HttpServletRequest request;
	
	private RuleExecutor rExecutor;
	
	private boolean isMobileRequest;
	
	private String accessToken;
	
	private String loginId;
	
	private String companyCode;
	
	//private IDataSet dataset;
	
	private RuleExtraData rExtra;
	
	private RuleContext(){
		
	}
	
	public static RuleContext createSingle(String loginId){
		RuleContext rContext = new RuleContext();
		rContext.loginId = StringKit.getString(loginId, TRANSPORT_SINGLE);
		
		//rContext.dataset = DataSet.getInstance();
		rContext.rExtra = RuleExtraData.create();
		if(logger.isDebugEnabled())
			logger.debug("loginId="+rContext.loginId);
		return rContext;
	}
	
	public static RuleContext create(HttpServletRequest request){
		//if (request == null)
		//	request = (HttpServletRequest) ThreadLocalBean.getInstance().getThreadAttribute("request");
		
		RuleContext rContext = new RuleContext();
		rContext.request = request;

		rContext.isMobileRequest = "true".equals(RuleKit.getStringParam(request, "mobile"));
		rContext.accessToken = RuleKit.getStringParam(request, "mtn");
		
		rContext.loginId = RuleKit.getStringParam(request, "loginId");
		if(StringKit.isEmpty(rContext.loginId))
			throw new RuleException(SubErrorType.ISV_MISSING_PARAMETER, "loginId");
		
		rContext.companyCode = RuleKit.getStringParam(request, JoyManager.getServer().getCompanyCodeParam());
		
		//rContext.dataset = DataSet.getInstance();
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
	
	public String prepareRemoteContextParam() {
		StringBuilder params = new StringBuilder();
		params.append("&loginId=").append(loginId);
		if(StringKit.isNotEmpty(companyCode))
			params.append("&"+JoyManager.getServer().getCompanyCodeParam()+"=").append(companyCode);
		if(logger.isDebugEnabled())
			logger.debug("prepareRemoteContextParam, params="+params);
		return params.toString();
	}
	
	public RuleResult invokeRule(String ruleURI, RuleParam rParam) throws Exception{
		return this.invokeRule(ruleURI, rParam, RuleInvokeConfig.create().setAsyn(false));
	}
	
	public RuleResult invokeRule(String ruleURI, RuleParam rParam, RuleInvokeConfig config) throws Exception{
		if(StringKit.isEmpty(ruleURI))
			return RuleResult.create().fail(SubError.createMain(SubErrorType.ISP_SERVICE_UNAVAILABLE, ruleURI));
		boolean isRemote = ruleURI.indexOf("@")>0;
		boolean isAsyn = false;
		if(config!=null)
			isAsyn = config.isAsyn();
		
		if(isRemote){
			return RuleExecutor.createRemote(this.cloneContext(), config).executeInner(ruleURI, rParam);
		}else{
			if(isAsyn){
				return RuleExecutor.create(this.cloneContext(), config).executeInner(ruleURI, rParam);
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
		//this.dataset = null;
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

	/*public IDataSet getDataset() {
		return dataset;
	}*/
	
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
