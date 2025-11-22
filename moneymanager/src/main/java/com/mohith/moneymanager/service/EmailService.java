package com.mohith.moneymanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {


    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private final WebClient webClient = WebClient.builder().build();

    public String sendEmail(String to, String subject, String body){
        String url = "https://api.brevo.com/v3/smtp/email";
        Map<String, Object> content = Map.of(
                "sender", Map.of(
                        "name", senderName,
                        "email", senderEmail
                ),
                "to", new Object[]{
                        Map.of("email", to)
                },
                "subject", subject,
                "htmlContent", body
        );

        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("api-key", apiKey)
                .bodyValue(content)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("Failed to send email")
                .block();
    }

    public String sendEmailWithAttachment(
            String to,
            String subject,
            String body,
            byte[] attachment,
            String filename
    ) {

        String url = "https://api.brevo.com/v3/smtp/email";

        // Base64 encode the file (Brevo requirement)
        String base64Attachment = Base64.getEncoder().encodeToString(attachment);

        Map<String, Object> content = Map.of(
                "sender", Map.of(
                        "name", senderName,
                        "email", senderEmail
                ),
                "to", new Object[]{
                        Map.of("email", to)
                },
                "subject", subject,
                "htmlContent", body,

                // Brevo requires attachments as base64 objects
                "attachment", new Object[]{
                        Map.of(
                                "name", filename,
                                "content", base64Attachment
                        )
                }
        );

        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("api-key", apiKey)
                .bodyValue(content)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("Failed to send email")
                .block();
    }

}
