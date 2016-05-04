package cn.joy.plugin.test.mail;

import org.testng.annotations.Test;

import cn.joy.framework.test.TestExecutor;
import cn.joy.plugin.mail.Mail;

@Test(groups="case.mail", dependsOnGroups="case.init")
public class MailTest {

	@Test(enabled = false)
	public static void main(String[] args) {
		TestExecutor.executePluginGroup("case.mail");
	}
	
	public void testSendText(){
		//Mail.sendTextMail("测试文本邮件发送", "1xx@qq.com", "这是一段文本。\n收到了吗？");
	}
	
	public void testSendHtml(){
		//Mail.sendHtmlMail("测试html邮件发送", "1xx@qq.com", "<h1>这是一段<b>html</b>。</h1><br><a href='http://www.baidu.com'>点击访问</a>");
	}
}


