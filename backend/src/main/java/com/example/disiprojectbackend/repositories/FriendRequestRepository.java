package com.example.disiprojectbackend.repositories;

import com.example.disiprojectbackend.entities.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {
    List<FriendRequest> findAllByReceiverId(UUID senderId);

    Optional<FriendRequest> findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(UUID senderId1, UUID receiverId1, UUID senderId2, UUID receiverId2);

}
