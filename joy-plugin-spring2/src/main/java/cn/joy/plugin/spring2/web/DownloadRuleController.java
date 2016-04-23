package cn.joy.plugin.spring2.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import cn.joy.framework.rule.RuleDispatcher;
/**
 * 下载业务规则接口调用控制器，负责客户端调用服务器提供的下载接口规则方法
 * @author liyy
 * @date 2014-08-15
 */
public class DownloadRuleController extends MultiActionController {
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RuleDispatcher.dispatchDownloadRule(request, response);
		return null;
	}
}
