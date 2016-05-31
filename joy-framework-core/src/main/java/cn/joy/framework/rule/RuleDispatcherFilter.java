package cn.joy.framework.rule;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
import cn.joy.framework.kits.StringKit;

public class RuleDispatcherFilter implements Filter{
	private static Log logger = LogKit.get();
	private String charset = "UTF-8";

	public void init(FilterConfig filterConfig) throws ServletException{
		charset = StringKit.getString(filterConfig.getInitParameter( "charset" ), charset);
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException{
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		request.setCharacterEncoding(charset);
		
		String servletPath = request.getServletPath();
		logger.debug("servletPath="+servletPath);
		
		servletPath = servletPath.substring(1);
		if(servletPath.equals(JoyManager.getServer().getUrlOpen())){
			RuleDispatcher.dispatchOpenRule(request, response);
		}else if(servletPath.startsWith(JoyManager.getServer().getUrlAPI()+"/")){
			RuleDispatcher.dispatchAPIRule(request, response);
		}else if(servletPath.equals(JoyManager.getServer().getUrlConfig())){
			RuleDispatcher.dispatchConfigService(request, response);
		}else if(servletPath.equals(JoyManager.getServer().getUrlBusiness())){
			String result = RuleDispatcher.dispatchBusinessRule(request, response);
			if(StringKit.isNotEmpty(result)){
				if(result.startsWith("redirect:")){
					response.sendRedirect(result.substring("redirect:".length()));
					return;
				}else if(result.startsWith("jsp:")){
					request.getRequestDispatcher(result.substring("jsp:".length())).forward(request, response);
					return;
				}
			}
		}else if(servletPath.equals(JoyManager.getServer().getUrlDownload())){
			RuleDispatcher.dispatchDownloadRule(request, response);
		}else if(servletPath.equals(JoyManager.getServer().getUrlWebProxy())){
			RuleDispatcher.dispatchWebProxy(request, response);
		}else
			chain.doFilter( request, response );
	}
	
	public void destroy(){
		
	}
}
