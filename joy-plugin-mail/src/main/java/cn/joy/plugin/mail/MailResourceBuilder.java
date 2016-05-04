package cn.joy.plugin.mail;

import javax.mail.Session;

import cn.joy.framework.plugin.PluginResourceBuilder;

public class MailResourceBuilder extends PluginResourceBuilder<MailResource>{
	private Session session;
	
	public MailResourceBuilder session(Session session){
		this.session = session;
		return this;
	}
	
	public MailResourceBuilder session(String key){
		this.session = Mail.plugin().getSession(key);
		return this;
	}
	
	@Override
	public MailResource build() {
		MailResource resource = new MailResource(session);
		return resource;
	}
	
}
