package cn.joy.framework.plugin.jfinal.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.joy.framework.kits.FileKit;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.JsonKit;
import cn.joy.framework.kits.NumberKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.jfinal.JfinalResource;
import cn.joy.framework.rule.RuleContext;
import cn.joy.framework.rule.RuleExecutor;
import cn.joy.framework.rule.RuleParam;
import cn.joy.framework.rule.RuleResult;

import com.jfinal.core.Controller;
/**
 * 下载业务规则接口调用控制器，负责客户端调用服务器提供的下载接口规则方法
 * @author liyy
 * @date 2014-09-10
 */
public class DownloadRuleController extends Controller {
	private Logger logger = Logger.getLogger(DownloadRuleController.class);
	
	public void index() {
		HttpServletRequest request = getRequest();
		HttpServletResponse response = getResponse();
		
		String service = getPara("_s");
		String action = getPara("_m");
		if(logger.isDebugEnabled())
			logger.debug("download controller rule invoke, service="+service+", action="+action);
		
		if(StringKit.isEmpty(service) || StringKit.isEmpty(action) ){
			HttpKit.writeResponse(response, "CHECK PARAMETER _s OR _m FAIL");
			return;
		}
		
		RuleResult checkResult = JfinalResource.getSecurityManager().checkBusinessRequest(request);
		if(!checkResult.isSuccess()){
			Map<String, Object> checkResultContent = checkResult.getMapFromContent();
			if(checkResultContent!=null && checkResultContent.containsKey("statusCode"))
				response.setStatus(NumberKit.getInteger(checkResultContent.get("statusCode"), 500));
			
			HttpKit.writeResponse(response, checkResult.getMsg());
			return;
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
		
		renderNull();
	}
}
