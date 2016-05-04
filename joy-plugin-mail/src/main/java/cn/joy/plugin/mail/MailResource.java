package cn.joy.plugin.mail;

import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import cn.joy.framework.kits.FileKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.PluginResource;

public class MailResource extends PluginResource {
	private Session session;

	MailResource(Session session) {
		this.session = session;
	}
	
	public Session getSession(){
		return this.session;
	}
	
	public void sendTextMail(String subject, String to, String content) {
		// 获得Transport实例对象
		Transport transport = null;
		try{
			// 创建MimeMessage实例对象
			MimeMessage message = new MimeMessage(session);
			// 设置发件人
			message.setFrom(new InternetAddress(session.getProperty("mail.smtp.user")));
			// 设置邮件主题
			message.setSubject(subject);
			// 设置收件人
			message.setRecipients(RecipientType.TO, InternetAddress.parse(to));
			// 设置发送时间
			message.setSentDate(new Date());
			// 设置纯文本内容为邮件正文
			message.setText(content);
			// 保存并生成最终的邮件内容
			/*message.saveChanges();
			
			transport = session.getTransport();
			// 打开连接
			transport.connect(session.getProperty("mail.smtp.user"), session.getProperty("mail.smtp.password"));
			// 将message对象传递给transport对象，将邮件发送出去
			transport.sendMessage(message, message.getAllRecipients());*/
			Transport.send(message, session.getProperty("mail.smtp.user"), session.getProperty("mail.smtp.password"));
		}catch(Exception e){
			log.error("", e);
			throw new RuntimeException(e);
		}finally{
			try {
				if(transport!=null)
					transport.close();
			} catch (MessagingException e) {
			}
		}
	}
	
	public void sendHtmlMail(String subject, String to, String htmlContent){
		try {
			MimeMessage message = new MimeMessage(session);
			message.setSubject(subject);
			message.setFrom(new InternetAddress(session.getProperty("mail.smtp.user")));
			message.setSentDate(new Date());
			message.setRecipients(RecipientType.TO, InternetAddress.parse(to));
			// 设置html内容为邮件正文，指定MIME类型为text/html类型，并指定字符编码为gbk
			message.setContent(htmlContent, "text/html;charset=gbk");
			// 发送邮件
			Transport.send(message, session.getProperty("mail.smtp.user"), session.getProperty("mail.smtp.password"));
		}catch(Exception e){
			log.error("", e);
			throw new RuntimeException(e);
		}
	}

	public void sendMail(String subject, String to, String cc, String bcc, String content, String contentType, String[] attachments){
		try {
			MimeMessage message = new MimeMessage(session);
			message.setSubject(subject);
			message.setFrom(new InternetAddress(session.getProperty("mail.smtp.user")));
			message.setRecipients(RecipientType.TO, InternetAddress.parse(to));
			message.setRecipients(RecipientType.CC, InternetAddress.parse(cc));
			message.setRecipients(RecipientType.BCC, InternetAddress.parse(bcc));
			message.setSentDate(new Date());

			if(attachments!=null && attachments.length>0){
				// 创建一个MIME子类型为"mixed"的MimeMultipart对象，表示这是一封混合组合类型的邮件
				MimeMultipart mailContent = new MimeMultipart("mixed");
				message.setContent(mailContent);
				// 内容
				MimeBodyPart mailBody = new MimeBodyPart();

				// 附件
				for(int i=0;i<attachments.length;i++){
					MimeBodyPart attach = new MimeBodyPart();
					mailContent.addBodyPart(attach);
					//利用jaf框架读取数据源生成邮件体
					DataSource ds = new FileDataSource(attachments[i]);
					DataHandler dh = new DataHandler(ds);
					attach.setFileName(MimeUtility.encodeText(FileKit.getFileName(attachments[i])));
					attach.setDataHandler(dh);
				}
				
				// 将附件和内容添加到邮件当中
				mailContent.addBodyPart(mailBody);

				if(StringKit.isNotEmpty(contentType)){
					// 邮件正文(内嵌图片+html文本)
					MimeMultipart body = new MimeMultipart("related"); // 邮件正文也是一个组合体,需要指明组合关系
					mailBody.setContent(body);

					// 邮件正文由html和图片构成
					MimeBodyPart htmlPart = new MimeBodyPart();
					body.addBodyPart(htmlPart);

					// html邮件内容
					MimeMultipart htmlMultipart = new MimeMultipart("alternative");
					htmlPart.setContent(htmlMultipart);
					MimeBodyPart htmlContent = new MimeBodyPart();
					htmlContent.setContent(content, contentType);
					htmlMultipart.addBodyPart(htmlContent);
				}else{
					mailBody.setText(content);
				}
			}else{
				if(StringKit.isNotEmpty(contentType))
					message.setContent(content, contentType);
				else
					message.setText(content);
			}

			Transport.send(message, session.getProperty("mail.smtp.user"), session.getProperty("mail.smtp.password"));
		} catch (Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}

	public void release() {
		this.session = null;
	}
}
