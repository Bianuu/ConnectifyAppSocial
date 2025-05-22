package com.example.disiprojectbackend.configs;

import com.example.disiprojectbackend.DTOs.ConnectionSuggestMessageDTO;
import com.example.disiprojectbackend.DTOs.FriendSuggestionDTO;
import com.example.disiprojectbackend.entities.User;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SuggestionMessageProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitTemplate.class);
    private final RabbitTemplate rabbitTemplate;

    public void sendSuggestionMessage(User user1, User user2, String actionMessage) {
        FriendSuggestionDTO friendSuggestionDTO1 = FriendSuggestionDTO.builder()
                .id(user1.getId())
                .username(user1.getUsername())
                .image(user1.getImage())
                .build();

        FriendSuggestionDTO friendSuggestionDTO2 = FriendSuggestionDTO.builder()
                .id(user2.getId())
                .username(user2.getUsername())
                .image(user2.getImage())
                .build();

        ConnectionSuggestMessageDTO connectionMessage = ConnectionSuggestMessageDTO.builder()
                .friendSuggestionDTO1(friendSuggestionDTO1)
                .friendSuggestionDTO2(friendSuggestionDTO2)
                .actionMessage(actionMessage)
                .build();

        LOGGER.info("Sending message to RabbitMQ: {}", connectionMessage);
        rabbitTemplate.convertAndSend("friendsQueue", connectionMessage);
        LOGGER.info("Message sent to RabbitMQ: {}", connectionMessage);
    }
}
