package com.example.disiprojectbackend.serviceInterfaces;

import com.example.disiprojectbackend.DTOs.FriendDTO;

import java.util.List;
import java.util.UUID;

public interface FriendInterface {

    UUID createFriend(FriendDTO friendDTO);

    FriendDTO getFriendById(UUID friendId);


    List<FriendDTO> getAllFriends();

    void deleteFriend(UUID id);

}
