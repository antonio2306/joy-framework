package cn.joy.framework.plugin.spring.web;

import java.io.File;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import cn.joy.framework.kits.FileKit;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.JsonKit;
import cn.joy.framework.kits.NumberKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.spring.SpringResource;
import cn.joy.framework.rule.RuleContext;
import cn.joy.framework.rule.RuleExecutor;
import cn.joy.framework.rule.RuleParam;
import cn.joy.framework.rule.RuleResult;
/**
 * 下载业务规则接口调用控制器，负责客户端调用服务器提供的下载接口规则方法
 * @author liyy
 * @date 2014-08-15
 */
public class DownloadRuleController extends MultiActionController {
	private Logger logger = Logger.getLogger(DownloadRuleController.class);
	
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String service = request.getParameter("_s");
		String action = request.getParameter("_m");
		if(logger.isDebugEnabled())
			logger.debug("download controller rule invoke, service="+service+", action="+action);
		
		if(StringKit.isEmpty(service) || StringKit.isEmpty(action) ){
			HttpKit.writeResponse(response, "CHECK PARAMETER _s OR _m FAIL");
			return null;
		}
		
		RuleResult checkResult = SpringResource.getSecurityManager().checkBusinessRequest(request);
		if(!checkResult.isSuccess()){
			Map<String, Object> checkResultContent = checkResult.getMapFromContent();
			if(checkResultContent!=null && checkResultContent.containsKey("statusCode"))
				response.setStatus(NumberKit.getInteger(checkResultContent.get("statusCode"), 500));
			
			HttpKit.writeResponse(response, checkResult.getMsg());
			return null;
		}
		
		String ruleURI = service+"."+service+"Controller#"+action;
		if(logger.isDebugEnabled())
			logger.debug("download controller rule invoke, ruleURI="+ruleURI);
		
		RuleParam rParam = (RuleParam)JsonKit.json2Object(request.getParameter("params"), RuleParam.class);
		if(rParam==null)
			rParam = RuleParam.create();
		
		RuleResult result = RuleExecutor.create(RuleContext.create(request)).execute(ruleURI, rParam);
		if(result.isSuccess()){
			FileKit.downloadFile(response, result.getMapFromContent());
		}else	
			HttpKit.writeResponse(response, result.toJSON());
		RuleExecutor.clearCurrentExecutor();
		
		return null;
	}
}
