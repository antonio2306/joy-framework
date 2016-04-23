package cn.joy.plugin.jfinal.web;

import cn.joy.framework.kits.StringKit;
import cn.joy.framework.rule.RuleDispatcher;

import com.jfinal.core.Controller;
/**
 * 通用业务规则接口调用控制器，负责客户端调用服务器提供的服务接口规则方法
 * @author liyy
 * @date 2014-09-10
 */
public class BusinessRuleController extends Controller {
	public void index() {
		String result = RuleDispatcher.dispatchBusinessRule(getRequest(), getResponse());

		if(StringKit.isNotEmpty(result)){
			if(result.startsWith("redirect:")){
				redirect(result.substring("redirect:".length()));
				return;
			}else if(result.startsWith("jsp:")){
				renderJsp(result.substring("jsp:".length()));
				return;
			}
		}
			
		renderNull();
	}
}
