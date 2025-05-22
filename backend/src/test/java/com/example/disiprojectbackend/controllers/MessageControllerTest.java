package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.MessageDTO;
import com.example.disiprojectbackend.serviceInterfaces.MessageInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MessageControllerTest {

    @Mock
    private MessageInterface messageInterface;

    @InjectMocks
    private MessageController messageController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getMessages - returns list")
    void getMessages_returnsList() {
        MessageDTO message = MessageDTO.builder()
                .id(UUID.randomUUID())
                .content("Hello")
                .build();
        when(messageInterface.getMessages()).thenReturn(List.of(message));

        var response = messageController.getMessages();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<MessageDTO> messages = (List<MessageDTO>) response.getBody();
        assertNotNull(messages);
        assertFalse(messages.isEmpty());
        assertEquals("Hello", messages.get(0).getContent());
    }

    @Test
    @DisplayName("getAllMessagesBetweenUsers - returns list")
    void getAllMessagesBetweenUsers_returnsList() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        MessageDTO message = MessageDTO.builder()
                .id(UUID.randomUUID())
                .content("Hi")
                .build();
        when(messageInterface.getAllMessagesBetweenUsers(senderId, receiverId)).thenReturn(List.of(message));

        var response = messageController.getMessagesBetweenUsers(senderId, receiverId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<MessageDTO> messages = (List<MessageDTO>) response.getBody();
        assertNotNull(messages);
        assertFalse(messages.isEmpty());
        assertEquals("Hi", messages.get(0).getContent());
    }

    @Test
    @DisplayName("insertMessage - validation fails")
    void insertMessage_validationFails() {
        MessageDTO message = MessageDTO.builder().content("").build();

        var response = messageController.insertMessage(message);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("insertMessage - success")
    void insertMessage_success() {
        MessageDTO message = MessageDTO.builder().content("Valid content").build();
        when(messageInterface.createMessage(any())).thenReturn(UUID.randomUUID());

        var response = messageController.insertMessage(message);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("updateMessage - validation fails")
    void updateMessage_validationFails() {
        MessageDTO message = MessageDTO.builder().content("").build();

        var response = messageController.updateMessage(message);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("updateMessage - success")
    void updateMessage_success() {
        MessageDTO message = MessageDTO.builder()
                .id(UUID.randomUUID())
                .content("Updated message")
                .build();

        var response = messageController.updateMessage(message);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(messageInterface).updateMessage(message);
    }

    @Test
    @DisplayName("deleteMessage - success")
    void deleteMessage_success() {
        UUID messageId = UUID.randomUUID();

        var response = messageController.deleteMessage(messageId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(messageInterface).deleteMessage(messageId);
    }
}
