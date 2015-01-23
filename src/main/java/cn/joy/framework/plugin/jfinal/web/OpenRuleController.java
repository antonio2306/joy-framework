package cn.joy.framework.plugin.jfinal.web;

import cn.joy.framework.rule.RuleDispatcher;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
/**
 * 通用开放规则调用控制器，负责中心服务器和各应用服务器之间的规则调用，同时负责中心服务器的配置获取服务
 * @author liyy
 * @date 2014-09-10
 */
@Before(OpenRuleInterceptor.class)
public class OpenRuleController extends Controller {
	public void index() {
		RuleDispatcher.dispatchOpenRule(getRequest(), getResponse());
		renderNull();
	}
	
	public void getConfig(){
		RuleDispatcher.dispatchConfigService(getRequest(), getResponse());
		renderNull();
	}

}
