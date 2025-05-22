package com.example.disiprojectbackend.configs;

import com.example.disiprojectbackend.DTOs.EmailDTO;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(EmailDTO emailDTO) {

        rabbitTemplate.convertAndSend("emailQueue", emailDTO);


    }
}
