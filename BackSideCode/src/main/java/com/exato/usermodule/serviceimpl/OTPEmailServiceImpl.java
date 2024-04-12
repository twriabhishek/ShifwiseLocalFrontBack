package com.exato.usermodule.serviceimpl;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.exato.usermodule.service.OTPEmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class OTPEmailServiceImpl implements OTPEmailService {
	
	private final JavaMailSender mailSender;
    private final Environment environment;

    @Autowired
    public OTPEmailServiceImpl(JavaMailSender mailSender, Environment environment) {
        this.mailSender = mailSender;
        this.environment = environment;
    }
	    
	    @Override
	    public boolean sendOTPEmail(String userEmail, String otp, String siteURL) {
	        String toAddress = userEmail;
	        String fromAddress = "sshiftwise@gmail.com";
	        String senderName = "Exato";
	        String subject = "Verification for forget password";
	        String content = "Dear [[name]],<br>" +
	                "Please find the otp : [[otp]] to reset the password:<br>" +
	                "<h3><a href=\"[[URL]]\" target=\"_self\">CLICK HERE</a></h3>" +
	                "Thank you,<br>" +
	                "Exato.";

	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper;
	        try {
	            helper = new MimeMessageHelper(message, true);
	            helper.setFrom(new InternetAddress(fromAddress, senderName));
	            helper.setTo(toAddress);
	            helper.setSubject(subject);
	            content = content.replace("[[name]]", userEmail);
	            content = content.replace("[[otp]]", otp);

	            String verifyURL = environment.getProperty("shiftwiseForgotLink")+userEmail;
	            content = content.replace("[[URL]]", verifyURL);
	            helper.setText(content, true);
	            mailSender.send(message);
	            return true;
	        } catch (MessagingException | UnsupportedEncodingException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

}
