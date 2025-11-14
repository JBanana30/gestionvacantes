package com.example.gestion_vacantes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envía un correo electrónico simple.
     * @param to Dirección de correo del destinatario.
     * @param subject Asunto del correo.
     * @param text Cuerpo del correo.
     */
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            // Si quieres que el remitente sea diferente al configurado, usa message.setFrom()
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            System.out.println("Correo enviado exitosamente a: " + to);
        } catch (Exception e) {
            System.err.println("Error al enviar correo a " + to + ": " + e.getMessage());
            // Considera usar un logger en lugar de System.err
            // logger.error("Error al enviar correo a {}: {}", to, e.getMessage());
        }
    }
}