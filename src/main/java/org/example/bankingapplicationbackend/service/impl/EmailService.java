package org.example.bankingapplicationbackend.service.impl;

import org.example.bankingapplicationbackend.dto.EmailDetails;

public interface EmailService {
    void sendEmail(EmailDetails emailDetails);
}
