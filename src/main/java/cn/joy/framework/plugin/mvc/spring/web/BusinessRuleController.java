package cn.joy.framework.plugin.mvc.spring.web;

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
		
		boolean isMobile = "true".equals(request.getParameter("mobile"));
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
				
				//TODO token
				/*IDataSet dataset = DataSet.getInstance();
				MobileSession ms = UserSession.dao.getSession(dataset, loginId, tn);
				if(ms==null){
					ms = UserSession.dao.getLastestSession(dataset, loginId);
					response.setStatus(518);
					HttpKit.writeResponse(response, "INVALID_TOKEN|"+JsonKit.object2Json(ms));
				}*/
			}
		}
			
		String ruleURI = service+"."+service+"Controller#"+action;
		if(logger.isDebugEnabled())
			logger.debug("business controller rule invoke, ruleURI="+ruleURI);
		
		RuleParam rParam = (RuleParam)JsonKit.json2Object(request.getParameter("params"), RuleParam.class);
		if(rParam==null)
			rParam = RuleParam.create();
		
		String isMergeRequest = RuleKit.getStringParam(request, "imr");
		if("y".equals(isMergeRequest)){
			String mergeKey = RuleKit.getStringParam(request, "mk");
			String[] keyValues = RuleKit.getStringParam(request, mergeKey).split(",");
			Map<String, String> mergeResult = new HashMap<String, String>();
			for(String kv:keyValues){
				request.setAttribute("MK_"+mergeKey, kv);
				//执行分离，事务分离，合并结果
				RuleResult result = RuleExecutor.create(RuleContext.create(request)).execute(ruleURI, rParam);
				content = result.toJSON();
				mergeResult.put(kv, content);
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
