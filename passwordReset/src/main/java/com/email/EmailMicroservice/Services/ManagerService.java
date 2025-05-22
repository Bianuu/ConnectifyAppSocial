package com.email.EmailMicroservice.Services;

import com.email.EmailMicroservice.DTOs.EmailDTO;

public interface ManagerService {

    public void sendEmail(EmailDTO emailDTO);
}
