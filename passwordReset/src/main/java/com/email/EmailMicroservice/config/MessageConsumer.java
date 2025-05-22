package com.email.EmailMicroservice.config;

import com.email.EmailMicroservice.DTOs.EmailDTO;
import com.email.EmailMicroservice.Services.ManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    private final ManagerService managerService;

    public MessageConsumer(ManagerService managerService) {
        this.managerService = managerService;
    }

    @RabbitListener(queues = "emailQueue")
    public void consumeMessage(EmailDTO emailDTO) {
        managerService.sendEmail(emailDTO);
    }
}
