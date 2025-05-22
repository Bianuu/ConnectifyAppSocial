package com.example.disiprojectbackend.DTOs.builders;

import com.example.disiprojectbackend.DTOs.MessageDTO;
import com.example.disiprojectbackend.entities.Message;
import com.example.disiprojectbackend.entities.User;
import org.modelmapper.ModelMapper;

public class MessageBuilder {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static MessageDTO toMessageDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .senderMessage(message.getSenderMessage() != null ? message.getSenderMessage().getId() : null)
                .receiverMessage(message.getReceiverMessage() != null ? message.getReceiverMessage().getId() : null)
                .sentAt(message.getSentAt())
                .content(message.getContent())
                .build();
    }

    public static Message toEntity(MessageDTO dto, User sender, User receiver) {
        return Message.builder()
                .id(dto.getId())
                .senderMessage(sender)
                .receiverMessage(receiver)
                .sentAt(dto.getSentAt())
                .content(dto.getContent())
                .build();
    }
}
