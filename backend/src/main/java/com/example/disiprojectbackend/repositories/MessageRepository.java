package com.example.disiprojectbackend.repositories;

import com.example.disiprojectbackend.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findAllBySenderMessage_IdAndReceiverMessage_Id(UUID senderId, UUID receiverId);
}
