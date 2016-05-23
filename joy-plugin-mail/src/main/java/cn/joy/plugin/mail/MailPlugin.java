package cn.joy.plugin.mail;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.mail.Session;

import cn.joy.framework.annotation.Plugin;
import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.PropKit.Prop;
import cn.joy.framework.plugin.ResourcePlugin;

@Plugin(key = "mail")
public class MailPlugin extends ResourcePlugin<MailResourceBuilder, MailResource> {
	private Session mainSession;
	private final Map<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();

	public static MailResourceBuilder builder() {
		return new MailResourceBuilder();
	}

	public static MailPlugin plugin() {
		return (MailPlugin) JoyManager.plugin("mail");
	}

	@Override
	public boolean start() {
		Prop prop = getConfig();
		String[] resources = prop.get("resources").split(",");

		for (String resource : resources) {
			Prop sessionProp = prop.getSubPropTrimPrefix(resource + ".");

			Session session = null;
			//getDefaultInstance可能出现Access to default session denied的错误
			/*if (mainSession == null) {
				session = Session.getDefaultInstance(sessionProp, new MailAuthenticator(
						prop.get(resource + ".mail.smtp.user"), prop.get(resource + ".mail.smtp.password")));
				mainSession = session;
			} else {
				session = Session.getInstance(sessionProp, new MailAuthenticator(prop.get(resource + ".mail.smtp.user"),
						prop.get(resource + ".mail.smtp.password")));
			}*/
			session = Session.getInstance(sessionProp.getProperties(), new MailAuthenticator(prop.get(resource + ".mail.smtp.user"),
					prop.get(resource + ".mail.smtp.password")));
			if (mainSession == null) 
				mainSession = session;

			sessionMap.put(resource, session);
		}

		this.mainResource = builder().session(mainSession).name("joyMailServer").build();
		log.info("mail plugin start success");
		return true;
	}

	@Override
	public void stop() {
		sessionMap.clear();
	}
	
	public void addSession(String key, Prop prop){
		sessionMap.put(key, Session.getInstance(prop.getProperties(), new MailAuthenticator(prop.get("mail.smtp.user"),
						prop.get("mail.smtp.password"))));
	}
	
	public Session getSession(String key){
		return sessionMap.get(key);
	}

	public static MailResource use() {
		return plugin().useResource();
	}

	public static MailResource use(String name) {
		return plugin().useResource(name);
	}
	
	public static MailResource use(String name, JoyCallback missCallback) {
		return plugin().useResource(name, missCallback);
	}

	public static void unuse(String name) {
		plugin().unuseResource(name);
	}
	
	public static void sendTextMail(String subject, String to, String content) {
		use().sendTextMail(subject, to, content);
	}

	public static void sendHtmlMail(String subject, String to, String content) {
		use().sendHtmlMail(subject, to, content);
	}

	public void sendMail(String subject, String to, String cc, String bcc, String content, String contentType,
			String[] attachments) {
		use().sendMail(subject, to, cc, bcc, content, contentType, attachments);
	}

}
