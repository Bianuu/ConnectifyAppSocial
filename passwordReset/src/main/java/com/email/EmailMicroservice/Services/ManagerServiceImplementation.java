package com.email.EmailMicroservice.Services;

import com.email.EmailMicroservice.DTOs.EmailDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;


@Service
@AllArgsConstructor
public class ManagerServiceImplementation implements ManagerService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(EmailDTO emailDTO) {
        try {
            // Create MimeMessage
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set email properties
            helper.setFrom("sergiucionte2003@gmail.com", "Connectify");
            helper.setTo(emailDTO.getEmail());
            helper.setSubject("🔐 Resetare parolă Connectify");

            // Build password reset link
            String resetLink = "http://localhost:3000/reset-password/" + emailDTO.getId().toString();

            // Create styled HTML email body
            String htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <style>
                            body {
                                font-family: Arial, sans-serif;
                                line-height: 1.6;
                                color: #333333;
                                max-width: 600px;
                                margin: 0 auto;
                            }
                            .container {
                                padding: 20px;
                                border: 1px solid #e0e0e0;
                                border-radius: 5px;
                                background-color: #f9f9f9;
                            }
                            .header {
                                text-align: center;
                                padding-bottom: 20px;
                                border-bottom: 1px solid #e0e0e0;
                                margin-bottom: 20px;
                            }
                            .logo {
                                font-size: 24px;
                                font-weight: bold;
                                color: #2c3e50;
                            }
                            .content {
                                padding: 15px 0;
                            }
                            .btn {
                                display: inline-block;
                                padding: 12px 24px;
                                background-color: #3498db;
                                color: #ffffff !important;
                                text-decoration: none;
                                border-radius: 4px;
                                font-weight: bold;
                                margin: 20px 0;
                                text-align: center;
                            }
                            .btn:hover {
                                background-color: #2980b9;
                            }
                            .footer {
                                margin-top: 20px;
                                padding-top: 20px;
                                border-top: 1px solid #e0e0e0;
                                font-size: 12px;
                                color: #777;
                                text-align: center;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <div class="logo">🔌 Connectify</div>
                            </div>
                            <div class="content">
                                <h2>Salut!</h2>
                                <p>Am primit o solicitare pentru resetarea parolei contului tău Connectify. Pentru a-ți reseta parola, 
                                te rugăm să faci click pe butonul de mai jos:</p>
                    
                                <div style="text-align: center;">
                                    <a href="%s" class="btn">Resetează Parola</a>
                                </div>
                    
                                <p>Dacă nu ai solicitat resetarea parolei, te rugăm să ignori acest email sau să contactezi echipa de suport dacă 
                                consideri că este o activitate suspectă.</p>
                    
                                <p>Link-ul va expira în 30 de minute din motive de securitate.</p>
                    
                                <p>Dacă întâmpini probleme cu butonul de mai sus, copiază și lipește următorul link în browserul tău:</p>
                                <p style="word-break: break-all; font-size: 14px; color: #555;">%s</p>
                            </div>
                            <div class="footer">
                                <p>Acesta este un email automat. Te rugăm să nu răspunzi la această adresă.</p>
                                <p>&copy; %d Connectify. Toate drepturile rezervate.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """.formatted(resetLink, resetLink, java.time.Year.now().getValue());

            // Set HTML content
            helper.setText(htmlContent, true);

            // Send email
            javaMailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}