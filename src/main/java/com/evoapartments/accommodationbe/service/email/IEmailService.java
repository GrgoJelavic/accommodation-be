package com.evoapartments.accommodationbe.service.email;

public interface IEmailService {
    void sendSimpleMailMessage(String name, String emailTo, String token);
    void sendMimeMessageWithAttachments(String name, String emailTo, String token);
    void sendMimeMessageWithEmbeddedFiles(String name, String emailTo, String token);
    void sendHtmlEmail(String name, String emailTo, String token);
    void sendHtmlEmailWithEmbeddedFiles(String name, String emailTo, String token);

}
