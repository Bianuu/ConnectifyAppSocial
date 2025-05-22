package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.ChatNotificationDTO;
import com.example.disiprojectbackend.DTOs.MessageDTO;
import com.example.disiprojectbackend.serviceInterfaces.MessageInterface;
import com.example.disiprojectbackend.validators.MessageValidator;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@Controller
@AllArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);
    private final MessageInterface messageInterface;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/all")
    public ResponseEntity<?> getMessages() {
        LOGGER.info("Getting all messages");
        List<MessageDTO> messageDTOS = null;

        messageDTOS = messageInterface.getMessages();


        return ResponseEntity.ok(messageDTOS);
    }

    @Transactional(readOnly = true)
    @GetMapping("/messagesUsers/{senderId}/{receiverId}")
    public ResponseEntity<?> getMessagesBetweenUsers(@PathVariable("senderId") UUID senderId, @PathVariable("receiverId") UUID receiverId) {
        LOGGER.info("Getting all messages");
        List<MessageDTO> messageDTOS = null;
        messageDTOS = messageInterface.getAllMessagesBetweenUsers(senderId, receiverId);
        return ResponseEntity.ok(messageDTOS);
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload MessageDTO chatMessage) {
        MessageDTO savedMsg = messageInterface.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiverMessage().toString(), "/queue/messages",
                new ChatNotificationDTO(
                        savedMsg.getId(),
                        savedMsg.getSenderMessage(),
                        savedMsg.getReceiverMessage(),
                        savedMsg.getContent()
                )
        );
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertMessage(@RequestBody MessageDTO messageDTO) {

        List<String> validationErrors = MessageValidator.validateWholeDataForPost(
                messageDTO.getContent());

        if (!validationErrors.isEmpty()) {
            Map<String, Object> response = Map.of(
                    "error", "Validation failed",
                    "details", validationErrors
            );
            LOGGER.info("Datele introduse nu sunt corecte: " + validationErrors);
            return ResponseEntity.badRequest().body(response);
        }

        UUID insertedId = messageInterface.createMessage(messageDTO);
        LOGGER.info("Message with id " + insertedId + " was inserted in the database");

        Map<String, Object> response = Map.of(
                "message", "Message inserted successfully",
                "userId", insertedId.toString()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateMessage(@RequestBody MessageDTO messageDTO) {
        List<String> validationErrors = MessageValidator.validateWholeDataForPost(
                messageDTO.getContent());

        if (!validationErrors.isEmpty()) {
            Map<String, Object> response = Map.of(
                    "error", "Validation failed",
                    "details", validationErrors
            );
            LOGGER.info("Datele introduse nu sunt corecte: " + validationErrors);
            return ResponseEntity.badRequest().body(response);
        }


        messageInterface.updateMessage(messageDTO);

        LOGGER.info("Message with id {} was updated", messageDTO.getId());

        Map<String, String> response = Map.of(
                "message", "User with id " + messageDTO.getId() + " has been updated successfully"
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable("id") UUID id) {
        messageInterface.deleteMessage(id);
        LOGGER.info("Message with id {} was deleted", id);
        return ResponseEntity.ok("Message with id " + id + " has been deleted successfully");
    }

}
