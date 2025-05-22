package com.example.disiprojectbackend.serviceInterfaces;

import com.example.disiprojectbackend.DTOs.FriendRequestDTO;

import java.util.List;
import java.util.UUID;

public interface FriendRequestInterface {

    UUID createFriendRequest(FriendRequestDTO friendRequestDTO);

    FriendRequestDTO getFriendRequestById(UUID friendRequestId);

    List<FriendRequestDTO> getAllFriendRequest();

    void updateFriendRequest(FriendRequestDTO friendRequestDTO);

    void deleteFriendRequest(UUID id);

    List<FriendRequestDTO> getAllFriendRequestsByReceiverId(UUID senderId);

}
