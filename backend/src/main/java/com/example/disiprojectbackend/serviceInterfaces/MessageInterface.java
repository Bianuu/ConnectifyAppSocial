package com.example.disiprojectbackend.serviceInterfaces;

import com.example.disiprojectbackend.DTOs.MessageDTO;

import java.util.List;
import java.util.UUID;

public interface MessageInterface {

    UUID createMessage(MessageDTO messageDTO);

    MessageDTO getMessageById(UUID messageId);


    List<MessageDTO> getAllMessagesBetweenUsers(UUID senderId, UUID receiverId);

    List<MessageDTO> getMessages();

    void updateMessage(MessageDTO messageDTO);

    void deleteMessage(UUID id);

    MessageDTO save(MessageDTO messageDTO);
}
