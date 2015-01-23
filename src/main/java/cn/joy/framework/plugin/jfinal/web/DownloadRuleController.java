package cn.joy.framework.plugin.jfinal.web;

import cn.joy.framework.rule.RuleDispatcher;

import com.jfinal.core.Controller;
/**
 * 下载业务规则接口调用控制器，负责客户端调用服务器提供的下载接口规则方法
 * @author liyy
 * @date 2014-09-10
 */
public class DownloadRuleController extends Controller {
	public void index() {
		RuleDispatcher.dispatchDownloadRule(getRequest(), getResponse());
		renderNull();
	}
}
