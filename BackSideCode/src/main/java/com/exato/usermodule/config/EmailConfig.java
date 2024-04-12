package com.exato.usermodule.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfig {
	
	
	 @Bean
	    public JavaMailSender javaMailSender() {
	        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	        mailSender.setHost("smtp.gmail.com");
	        mailSender.setPort(587); // Replace with your SMTP server port
	        mailSender.setUsername("sshiftwise@gmail.com");
	        mailSender.setPassword("bsxxczvcpzkfjswu");

	        Properties props = mailSender.getJavaMailProperties();


			  props.put("mail.smtp.host", "smtp.gmail.com"); props.put("mail.smtp.port",
			  "587"); props.put("mail.smtp.auth", "true");
			  props.put("mail.smtp.ssl.protocols", "TLSv1.2");
			  props.put("mail.smtp.starttls.enable", "true");
			 

	        return mailSender;
	    }  
	 
	

	 }


