package com.exato.usermodule.serviceimpl;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.exato.usermodule.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
	
	private final JavaMailSender mailSender;
    private final Environment environment;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, Environment environment) {
        this.mailSender = mailSender;
        this.environment = environment;
    }
    
    @Override
    public boolean sendResetPasswordEmail(String toAddress, String resetLink, String token) {
        // Your email sending logic here
        // Use the mailSender and environment properties as needed

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(new InternetAddress("sshiftwise@gmail.com", "Exato"));
            helper.setTo(toAddress);
            helper.setSubject("Please reset your password");

            String content = "Dear [[name]],<br>" +
                    "Please click the link below to reset your password:<br>" +
                    "<h3><a href=\"[[URL]]\" target=\"_self\">RESET</a></h3>" +
                    "Link will get expire in next 12 hrs . Please reset your password within the time limit !!<br>"+
                    "</br>"+
                    "Thank you,<br>" +
                    "Exato";

            content = content.replace("[[name]]", toAddress);

            String verifyURL = environment.getProperty("shiftwiseResetLink") + toAddress +"/"+ token;

            content = content.replace("[[URL]]", verifyURL);

            helper.setText(content, true);

            mailSender.send(message);
            return true;
        } catch (UnsupportedEncodingException | MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }


}
