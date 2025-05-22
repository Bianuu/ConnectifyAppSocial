package com.example.friendships.configs;

import com.example.friendships.dtos.ConnectionSuggestMessageDTO;
import com.example.friendships.services.SuggestionService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SuggestionConsumer {

    private final SuggestionService suggestionService;

    @RabbitListener(queues = "friendsQueue")
    public void receiveMessage(ConnectionSuggestMessageDTO message) {
        try {
            if ("insert".equalsIgnoreCase(message.getActionMessage())) {
                suggestionService.handleFriendSuggestion(message.getFriendSuggestionDTO1(), message.getFriendSuggestionDTO2());
                System.out.printf("Processed friendship between %s and %s%n",
                        message.getFriendSuggestionDTO1().getUsername(),
                        message.getFriendSuggestionDTO2().getUsername());
            } else if ("delete".equalsIgnoreCase(message.getActionMessage())) {
                suggestionService.handleFriendDeletion(message.getFriendSuggestionDTO1(), message.getFriendSuggestionDTO2());
                System.out.printf("Processed friendship deletion between %s and %s%n",
                        message.getFriendSuggestionDTO1().getUsername(),
                        message.getFriendSuggestionDTO2().getUsername());
            } else if ("user_delete".equalsIgnoreCase(message.getActionMessage())) {
                suggestionService.handleUserDeletion(message.getFriendSuggestionDTO1().getId());
                System.out.printf("Processed user deletion for %s%n", message.getFriendSuggestionDTO1().getUsername());
            } else if ("update".equalsIgnoreCase(message.getActionMessage())) {
                suggestionService.handleUserUpdate(message.getFriendSuggestionDTO1());
                System.out.printf("Processed user update for %s%n", message.getFriendSuggestionDTO1().getUsername());
            } else {
                System.out.println("Unknown action message: " + message.getActionMessage());
            }
        } catch (Exception e) {
            // Log and suppress to prevent consumer thread from dying
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
