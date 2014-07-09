package cn.joy.framework.plugin.spring.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.JsonKit;
import cn.joy.framework.kits.RuleKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.spring.SpringResource;
import cn.joy.framework.rule.RuleContext;
import cn.joy.framework.rule.RuleExecutor;
import cn.joy.framework.rule.RuleParam;
import cn.joy.framework.rule.RuleResult;
/**
 * 通用业务规则接口调用控制器
 * @author liyy
 * @date 2014-06-12
 */
public class BusinessRuleController extends MultiActionController {
	private Logger logger = Logger.getLogger(BusinessRuleController.class);
	
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String content = "";
		String service = request.getParameter("_s");
		String action = request.getParameter("_m");
		if(logger.isDebugEnabled())
			logger.debug("business controller rule invoke, service="+service+", action="+action);
		
		if(StringKit.isEmpty(service) || StringKit.isEmpty(action) ){
			HttpKit.writeResponse(response, "CHECK PARAMETER _s OR _m FAIL");
			return null;
		}
		
		if(SpringResource.getSecurityManager()!=null){
			RuleResult checkResult = SpringResource.getSecurityManager().checkBusinessRequest(request);
			if(!checkResult.isSuccess()){
				HttpKit.writeResponse(response, checkResult.toJSON());
				return null;
			}
		}
		
		/*boolean isMobile = "true".equals(request.getParameter("mobile"));
		if(isMobile){
			if(StringKit.isEmpty(request.getParameter("versionID"))){
				HttpKit.writeResponse(response, "CHECK PARAMETER versionID FAIL");
				return null;
			}
			
			String loginId = request.getParameter("loginId");
			if(StringKit.isEmpty(loginId)){
				HttpKit.writeResponse(response, "CHECK PARAMETER loginId FAIL");
				return null;
			}
			if(!"account".equals(service)){
				String tn = request.getParameter("mtn");
				if(StringKit.isEmpty(tn)){
					HttpKit.writeResponse(response, "CHECK PARAMETER mtn FAIL");
					return null;
				}
				
				IDataSet dataset = DataSet.getInstance();
				MobileSession ms = UserSession.dao.getSession(dataset, loginId, tn);
				if(ms==null){
				//去网站验证tn是否为最新token，若是，则更新本地token
				RuleResult checkResult = RuleExecutor.createRemote(RuleContext.create(request)).execute("website@account.accountService#checkAccessToken", 
					RuleParam.create().put("loginId", request.getParameter("loginId")).put("tn", tn));
				if(checkResult.isSuccess() && checkResult.getContent()==Boolean.TRUE){
					ms = new MobileSession(tn, request.getParameter("loginId"), "sync from website", new Date());
					AuthAppRegister.getCustomer().insertMobileSession(ms);
				}
				
					ms = UserSession.dao.getLastestSession(dataset, loginId);
					response.setStatus(518);
					HttpKit.writeResponse(response, "INVALID_TOKEN|"+JsonKit.object2Json(ms));
				}
			}
		}*/
			
		String ruleURI = service+"."+service+"Controller#"+action;
		if(logger.isDebugEnabled())
			logger.debug("business controller rule invoke, ruleURI="+ruleURI);
		
		RuleParam rParam = (RuleParam)JsonKit.json2Object(request.getParameter("params"), RuleParam.class);
		if(rParam==null)
			rParam = RuleParam.create();
		
		String isMergeRequest = RuleKit.getStringParam(request, "imr");
		if(logger.isDebugEnabled())
			logger.debug("isMergeRequest="+isMergeRequest);
		if("y".equals(isMergeRequest)){
			String mergeKey = RuleKit.getStringParam(request, "mk");
			if(logger.isDebugEnabled())
				logger.debug("mergeKey="+mergeKey+", keyValues="+RuleKit.getStringParam(request, mergeKey));
			String[] keyValues = RuleKit.getStringParam(request, mergeKey).split(",");
			Map<String, RuleResult> mergeResult = new HashMap<String, RuleResult>();
			for(String kv:keyValues){
				if(StringKit.isEmpty(kv))
					continue;
				request.setAttribute("MK_"+mergeKey, kv);
				//执行分离，事务分离，合并结果
				RuleResult result = RuleExecutor.create(RuleContext.create(request)).execute(ruleURI, rParam);
				//content = result.toJSON();
				if(logger.isDebugEnabled())
					logger.debug("kv="+kv+", content="+result.toJSON());
				mergeResult.put(kv, result);
				RuleExecutor.clearCurrentExecutor();
			}
			HttpKit.writeResponse(response, JsonKit.object2Json(mergeResult));
		}else{
			RuleResult result = RuleExecutor.create(RuleContext.create(request)).execute(ruleURI, rParam);
			content = result.toJSON();
			HttpKit.writeResponse(response, content);
			RuleExecutor.clearCurrentExecutor();
		}
		
		return null;
	}
}
