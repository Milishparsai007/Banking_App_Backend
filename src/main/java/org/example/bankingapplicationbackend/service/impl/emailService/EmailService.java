package org.example.bankingapplicationbackend.service.impl.emailService;

import org.example.bankingapplicationbackend.dto.EmailDetails;

public interface EmailService {
    void sendEmail(EmailDetails emailDetails);
    public void sendEmailWithAttachment(EmailDetails emailDetails);
}
