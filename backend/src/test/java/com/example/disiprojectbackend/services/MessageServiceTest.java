package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.MessageDTO;
import com.example.disiprojectbackend.entities.Message;
import com.example.disiprojectbackend.entities.User;
import com.example.disiprojectbackend.repositories.MessageRepository;
import com.example.disiprojectbackend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageRepository = mock(MessageRepository.class);
        userRepository = mock(UserRepository.class);
        messageService = new MessageService(messageRepository, userRepository, new ModelMapper());
    }

    @Test
    void createMessage_success() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        User sender = new User();
        sender.setId(senderId);
        User receiver = new User();
        receiver.setId(receiverId);

        MessageDTO dto = MessageDTO.builder()
                .senderMessage(senderId)
                .receiverMessage(receiverId)
                .content("Hello")
                .build();

        Message savedMessage = new Message();
        savedMessage.setId(UUID.randomUUID());

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(messageRepository.save(any())).thenReturn(savedMessage);

        UUID result = messageService.createMessage(dto);

        assertNotNull(result);
        verify(messageRepository).save(any());
    }

    @Test
    void createMessage_senderNotFound_throwsException() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        when(userRepository.findById(senderId)).thenReturn(Optional.empty());

        MessageDTO dto = MessageDTO.builder()
                .senderMessage(senderId)
                .receiverMessage(receiverId)
                .build();

        assertThrows(EntityNotFoundException.class,
                () -> messageService.createMessage(dto));
    }

    @Test
    void createMessage_receiverNotFound_throwsException() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        when(userRepository.findById(senderId)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(receiverId)).thenReturn(Optional.empty());

        MessageDTO dto = MessageDTO.builder()
                .senderMessage(senderId)
                .receiverMessage(receiverId)
                .build();

        assertThrows(EntityNotFoundException.class,
                () -> messageService.createMessage(dto));
    }

    @Test
    void getAllMessagesBetweenUsers_success() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        Message message = new Message();
        message.setId(UUID.randomUUID());
        message.setContent("Test");

        when(messageRepository.findAllBySenderMessage_IdAndReceiverMessage_Id(senderId, receiverId))
                .thenReturn(List.of(message));

        var result = messageService.getAllMessagesBetweenUsers(senderId, receiverId);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllMessagesBetweenUsers_noMessages_throwsException() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        when(messageRepository.findAllBySenderMessage_IdAndReceiverMessage_Id(senderId, receiverId))
                .thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class,
                () -> messageService.getAllMessagesBetweenUsers(senderId, receiverId));
    }

    @Test
    void getMessageById_found() {
        UUID messageId = UUID.randomUUID();
        Message message = new Message();
        message.setId(messageId);
        message.setContent("Hello");

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        assertNotNull(messageService.getMessageById(messageId));
    }

    @Test
    void getMessageById_notFound() {
        UUID messageId = UUID.randomUUID();

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> messageService.getMessageById(messageId));
    }

    @Test
    void getMessages_success() {
        Message message = new Message();
        message.setId(UUID.randomUUID());
        message.setContent("Hello");

        when(messageRepository.findAll()).thenReturn(List.of(message));

        var result = messageService.getMessages();

        assertEquals(1, result.size());
    }

    @Test
    void updateMessage_found() {
        UUID messageId = UUID.randomUUID();
        Message message = new Message();
        message.setId(messageId);
        message.setContent("Old");

        MessageDTO dto = MessageDTO.builder()
                .id(messageId)
                .content("New")
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        messageService.updateMessage(dto);

        verify(messageRepository).save(message);
        assertEquals("New", message.getContent());
    }

    @Test
    void deleteMessage_found() {
        UUID messageId = UUID.randomUUID();
        Message message = new Message();
        message.setId(messageId);

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        messageService.deleteMessage(messageId);

        verify(messageRepository).deleteById(messageId);
    }

    @Test
    void deleteMessage_notFound() {
        UUID messageId = UUID.randomUUID();

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> messageService.deleteMessage(messageId));
    }
}
