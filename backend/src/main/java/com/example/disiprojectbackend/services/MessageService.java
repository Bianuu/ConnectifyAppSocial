package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.MessageDTO;
import com.example.disiprojectbackend.DTOs.builders.MessageBuilder;
import com.example.disiprojectbackend.entities.Message;
import com.example.disiprojectbackend.entities.User;
import com.example.disiprojectbackend.repositories.MessageRepository;
import com.example.disiprojectbackend.repositories.UserRepository;
import com.example.disiprojectbackend.serviceInterfaces.MessageInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Builder
@AllArgsConstructor
public class MessageService implements MessageInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private ModelMapper modelMapper;

    @Override
    public UUID createMessage(MessageDTO messageDTO) {
        User user1 = userRepository.findById(messageDTO.getSenderMessage())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id:" + messageDTO.getSenderMessage()));
        User user2 = userRepository.findById(messageDTO.getReceiverMessage())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id:" + messageDTO.getReceiverMessage()));
        messageDTO.setSentAt(new Date());
        Message message = MessageBuilder.toEntity(messageDTO, user1, user2);
        message = messageRepository.save(message);
        LOGGER.info("Message with id {} inserted", message.getId());
        return message.getId();
    }

    public List<MessageDTO> getAllMessagesBetweenUsers(UUID senderId, UUID receiverId) {
        List<Message> messages = messageRepository.findAllBySenderMessage_IdAndReceiverMessage_Id(senderId, receiverId);
        if (messages.isEmpty()) {
            LOGGER.error("User with id {} has no messages with user with id {}", senderId, receiverId);
            throw new EntityNotFoundException("User has no albums");
        }
        return messages.stream().map(MessageBuilder::toMessageDTO).collect(Collectors.toList());
    }

    public MessageDTO save(MessageDTO messageDTO) {
        User user1 = userRepository.findById(messageDTO.getSenderMessage())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id:" + messageDTO.getSenderMessage()));
        User user2 = userRepository.findById(messageDTO.getReceiverMessage())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id:" + messageDTO.getReceiverMessage()));
        Message message = MessageBuilder.toEntity(messageDTO, user1, user2);

        messageRepository.save(message);
        return messageDTO;
    }


    @Override
    public MessageDTO getMessageById(UUID messageId) {
        Optional<Message> messageOptional = messageRepository.findById(messageId);
        if (!messageOptional.isPresent()) {

            LOGGER.error("Message with id {} was not found", messageId);
        }
        return MessageBuilder.toMessageDTO(messageOptional.get());
    }


    @Override
    public List<MessageDTO> getMessages() {
        List<Message> messages = messageRepository.findAll();
        return messages.stream().map(MessageBuilder::toMessageDTO).collect(Collectors.toList());

    }

    @Override
    public void updateMessage(MessageDTO messageDTO) {

        Optional<Message> messageOptional = messageRepository.findById(messageDTO.getId());
        if (!messageOptional.isPresent()) {

            LOGGER.info("Message with id {} was not found", messageDTO.getId());
        }
        Message message = messageOptional.get();
        message.setContent(messageDTO.getContent());
        messageRepository.save(message);
        LOGGER.info("Message with id {} was updated in db ", messageDTO.getId());

    }


    @Override
    public void deleteMessage(UUID id) {
        Optional<Message> messageOptional = messageRepository.findById(id);
        if (!messageOptional.isPresent()) {
            LOGGER.error("Message request with id {} was not found in db", id);
        }
        messageRepository.deleteById(id);
        LOGGER.info("Message with id {} was deleted from db", id);

    }
}
