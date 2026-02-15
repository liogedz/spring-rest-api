package ee.lio.service.impl;

import ee.lio.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Override
    public void send2FACode(String to,
                            String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your 2FA Code");
        message.setText("Your verification code is: " + code);
        mailSender.send(message);
    }

    @Override
    public void sendPasswordReset(String email,
                                  String address) {
        String content = "To reset your password please click following link:\n\n" +
                address;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset your password");
        message.setText(content);
        mailSender.send(message);
    }
}
