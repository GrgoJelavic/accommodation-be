package com.evoapartments.accommodationbe.service.email.impl;

import com.evoapartments.accommodationbe.service.email.IEmailService;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;
import java.util.Objects;

import static com.evoapartments.accommodationbe.utils.EmailUtils.getEmailMessage;
import static com.evoapartments.accommodationbe.utils.EmailUtils.getVerificationUrl;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {
    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New user account verification";
    public static final String UTF_8_ENCODING = "UTF-8";
    private static final String EMAIL_TEMPLATE = "emailTemplate";
    public static final String CONTENT_TYPE = "text/html";
    private final TemplateEngine templateEngine;
    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String emailFrom;
    private final JavaMailSender emailSender;

    @Override
    @Async
    public void sendSimpleMailMessage(String name, String emailTo, String token) {
        try {
            SimpleMailMessage emailMessage = new SimpleMailMessage();
            emailMessage.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            emailMessage.setFrom(emailFrom);
            emailMessage.setTo(emailTo);
            emailMessage.setText(getEmailMessage(name, host, token));
            emailSender.send(emailMessage);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    @Async
    public void sendMimeMessageWithAttachments(String name, String emailTo, String token) {
        try {
            MimeMessage mimeMessage = getMimeMessage();
            MimeMessageHelper mimeHelper = getMimeMessageHelper(name, emailTo, token, mimeMessage);
            FileSystemResource soba = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/soba.jpg"));
            mimeHelper.addAttachment(Objects.requireNonNull(soba.getFilename()), soba);
            emailSender.send(mimeMessage);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    @Async
    public void sendMimeMessageWithEmbeddedFiles(String name, String emailTo, String token) {
        try {
            MimeMessage mimeMessage = getMimeMessage();
            getMimeMessageHelper(name, emailTo, token, mimeMessage);
            emailSender.send(mimeMessage);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    @Async
    public void sendHtmlEmail(String name, String emailTo, String token) {
        try {
            Context context = new Context();
            context.setVariables(Map.of("firstName", name, "url", getVerificationUrl(host, token)));
            String text = templateEngine.process(EMAIL_TEMPLATE, context);
            MimeMessage mimeMessage = getMimeMessage();
            MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, true, UTF_8_ENCODING);
            mimeHelper.setPriority(1);
            mimeHelper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            mimeHelper.setFrom(emailFrom);
            mimeHelper.setTo(emailTo);
            mimeHelper.setText(text, true);
            emailSender.send(mimeMessage);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    @Async
    public void sendHtmlEmailWithEmbeddedFiles(String name, String emailTo, String token) {
        try {
            MimeMessage mimeMessage = getMimeMessage();
            MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, true, UTF_8_ENCODING);
            mimeHelper.setPriority(1);
            mimeHelper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            mimeHelper.setFrom(emailFrom);
            mimeHelper.setTo(emailTo);
            Context context = new Context();
            context.setVariables(Map.of("firstName", name, "url", getVerificationUrl(host, token)));
            String text = templateEngine.process(EMAIL_TEMPLATE, context);
            mimeHelper.setText(text, true);
            MimeMultipart mimeMultipart = new MimeMultipart("related");
            BodyPart bodyPart = getTextBodyPart(text);
            mimeMultipart.addBodyPart(bodyPart);
            bodyPart = getImageBodyPart();
            mimeMultipart.addBodyPart(bodyPart);
            mimeMessage.setContent(mimeMultipart);
            emailSender.send(mimeMessage);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private MimeMessageHelper getMimeMessageHelper(String name, String emailTo, String token, MimeMessage mimeMessage) throws MessagingException {
        MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, true, UTF_8_ENCODING);
        mimeHelper.setPriority(1);
        mimeHelper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
        mimeHelper.setFrom(emailFrom);
        mimeHelper.setTo(emailTo);
        mimeHelper.setText(getEmailMessage(name, host, token));
        return mimeHelper;
    }

    private static BodyPart getImageBodyPart() throws MessagingException {
        BodyPart imageBodyPart = new MimeBodyPart();
        DataSource dataSource = new FileDataSource(System.getProperty("user.home") + "/Downloads/images/soba.jpg");
        imageBodyPart.setDataHandler(new DataHandler(dataSource));
        imageBodyPart.setHeader("Content-ID", "image");
        return imageBodyPart;
    }

    private static BodyPart getTextBodyPart(String text) throws MessagingException {
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(text, CONTENT_TYPE);
        return messageBodyPart;
    }

    private MimeMessage getMimeMessage() {
        return emailSender.createMimeMessage();
    }

    private String getContentId(String fileName) {
        return "<" + fileName + ">";
    }
}
