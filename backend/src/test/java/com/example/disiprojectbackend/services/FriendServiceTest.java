package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.FriendDTO;
import com.example.disiprojectbackend.configs.SuggestionMessageProducer;
import com.example.disiprojectbackend.entities.Friend;
import com.example.disiprojectbackend.entities.User;
import com.example.disiprojectbackend.repositories.FriendRepository;
import com.example.disiprojectbackend.repositories.FriendRequestRepository;
import com.example.disiprojectbackend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendServiceTest {

    private FriendRepository friendRepository;
    private UserRepository userRepository;
    private FriendRequestRepository friendRequestRepository;
    private SuggestionMessageProducer suggestionMessageProducer;
    private FriendService friendService;

    @BeforeEach
    void setUp() {
        friendRepository = mock(FriendRepository.class);
        userRepository = mock(UserRepository.class);
        friendRequestRepository = mock(FriendRequestRepository.class);
        suggestionMessageProducer = mock(SuggestionMessageProducer.class);

        friendService = new FriendService(
                friendRepository,
                userRepository,
                friendRequestRepository,
                suggestionMessageProducer,
                new ModelMapper()
        );
    }

    @Test
    void createFriend_success() {
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        FriendDTO friendDTO = FriendDTO.builder()
                .user1Id(user1Id)
                .user2Id(user2Id)
                .build();

        User user1 = new User();
        user1.setId(user1Id);
        User user2 = new User();
        user2.setId(user2Id);

        Friend friend = new Friend();
        friend.setId(UUID.randomUUID());
        friend.setUser1(user1);
        friend.setUser2(user2);

        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2Id)).thenReturn(Optional.of(user2));
        when(friendRepository.save(any())).thenReturn(friend);

        UUID result = friendService.createFriend(friendDTO);

        assertNotNull(result);
        verify(friendRepository).save(any(Friend.class));
        verify(suggestionMessageProducer).sendSuggestionMessage(user1, user2, "insert");
        verify(friendRequestRepository).findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
                eq(user1Id), eq(user2Id), eq(user2Id), eq(user1Id)
        );
    }

    @Test
    void createFriend_user1NotFound() {
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        FriendDTO friendDTO = FriendDTO.builder()
                .user1Id(user1Id)
                .user2Id(user2Id)
                .build();

        when(userRepository.findById(user1Id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> friendService.createFriend(friendDTO));
    }

    @Test
    void createFriend_user2NotFound() {
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        FriendDTO friendDTO = FriendDTO.builder()
                .user1Id(user1Id)
                .user2Id(user2Id)
                .build();

        User user1 = new User();
        user1.setId(user1Id);

        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2Id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> friendService.createFriend(friendDTO));
    }

    @Test
    void getFriendById_found() {
        UUID friendId = UUID.randomUUID();
        Friend friend = new Friend();
        friend.setId(friendId);
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        User user2 = new User();
        user2.setId(UUID.randomUUID());
        friend.setUser1(user1);
        friend.setUser2(user2);

        when(friendRepository.findById(friendId)).thenReturn(Optional.of(friend));

        FriendDTO result = friendService.getFriendById(friendId);

        assertNotNull(result);
        assertEquals(friendId, result.getId());
    }

    @Test
    void getAllFriends_success() {
        Friend friend = new Friend();
        friend.setId(UUID.randomUUID());
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        User user2 = new User();
        user2.setId(UUID.randomUUID());
        friend.setUser1(user1);
        friend.setUser2(user2);

        when(friendRepository.findAll()).thenReturn(Collections.singletonList(friend));

        var result = friendService.getAllFriends();

        assertEquals(1, result.size());
        assertEquals(friend.getId(), result.get(0).getId());
    }

    @Test
    void deleteFriend_exists_success() {
        UUID friendId = UUID.randomUUID();
        Friend friend = new Friend();
        friend.setId(friendId);
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        User user2 = new User();
        user2.setId(UUID.randomUUID());
        friend.setUser1(user1);
        friend.setUser2(user2);

        when(friendRepository.findById(friendId)).thenReturn(Optional.of(friend));

        friendService.deleteFriend(friendId);

        verify(friendRepository).deleteById(friendId);
        verify(suggestionMessageProducer).sendSuggestionMessage(user1, user2, "delete");
    }
}
