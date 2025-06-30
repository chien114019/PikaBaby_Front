package com.example.demo.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class SendMailUtils {	
	private static final String SMTP_HOST = "smtp.gmail.com";
	private static final String SMTP_USER = "bul199356@gmail.com";
	private static final String SMTP_PWD = "fvay eolx zszx kmyt";
	
	private static final String subject = "會員註冊信箱驗證";
	private static final String content = "親愛的 %s，"
			+ "\n感謝您註冊 PikaBaby 嬰幼兒用品專賣店會員~~"
			+ "\n您的電子信箱認證碼是：%s。"
			+ "\n請妥善保管此驗證碼，勿隨意外流。";
	
	
	private static String verifyCode = "";
//	private String emailAddr = "";
	
	private static Session getSession() {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");	//	SSL 加密
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", "465");
        
        System.out.println("props:" + props);
        
        Session session = Session.getInstance(props, new Authenticator() {
        	@Override
        	protected PasswordAuthentication getPasswordAuthentication() {
        		System.out.println("PasswordAuthentication");
        		return new PasswordAuthentication(SMTP_USER, SMTP_PWD);
        	}
        });
        
        return session;
	}
	
	public static void sendVerifyEmail(String toEmail, String custName) throws Exception {
		Session session = getSession();
		System.out.println(session);
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(SMTP_USER));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
		msg.setSubject(subject);
		
		verifyCode = createCode();
		msg.setText(String.format(content, custName, verifyCode));
		
		
		Transport.send(msg);
	}
	
	private static String createCode() {
		HashSet<Integer> codeSet = new HashSet();
		List<String> codeList = new ArrayList();
		
		while (codeSet.size() < 6) {
			codeSet.add((int)(Math.random() * 10));
//			System.out.println(codeSet);
		}
		
		for (Integer num : codeSet) {
			codeList.add(num.toString());
		}
		
		Collections.shuffle(codeList);
		
		String verifyCode = "";
		
		for (String str : codeList) {
			verifyCode += str;
		}
		
		System.out.println(verifyCode);
		return verifyCode;
	}
	
	public static boolean verifySuccess(String code) {
		return verifyCode.equals(code);
	}
	
	public static void sendEmail(String toEmail, String subject, String content) throws Exception {
	    Session session = getSession();
	    Message msg = new MimeMessage(session);
	    msg.setFrom(new InternetAddress(SMTP_USER));
	    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
	    msg.setSubject(subject);
	    msg.setText(content);
	    Transport.send(msg);
	}

}
