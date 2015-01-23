package cn.joy.framework.plugin.spring.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;

import cn.joy.framework.kits.StringKit;
import cn.joy.framework.rule.RuleDispatcher;
/**
 * 通用业务规则接口调用控制器，负责客户端调用服务器提供的服务接口规则方法
 * @author liyy
 * @date 2014-06-12
 */
public class BusinessRuleController extends MultiActionController {
	private Logger logger = Logger.getLogger(BusinessRuleController.class);
	
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String result = RuleDispatcher.dispatchBusinessRule(request, response);
		
		if(StringKit.isNotEmpty(result)){
			if(result.startsWith("redirect:"))
				return new ModelAndView(new RedirectView(result.substring("redirect:".length())));
			else if(result.startsWith("jsp:"))
				return new ModelAndView(result.substring("jsp:".length()));
		}
			
		return null;
	}
}
