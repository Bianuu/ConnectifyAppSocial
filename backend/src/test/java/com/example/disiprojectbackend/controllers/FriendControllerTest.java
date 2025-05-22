package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.FriendDTO;
import com.example.disiprojectbackend.serviceInterfaces.FriendInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FriendControllerTest {

    @Mock
    private FriendInterface friendInterface;

    @InjectMocks
    private FriendController friendController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getFriends - returns friends list")
    void getFriends_returnsFriends() {
        List<FriendDTO> friends = List.of(FriendDTO.builder()
                .id(UUID.randomUUID())
                .user1Id(UUID.randomUUID())
                .user2Id(UUID.randomUUID())
                .build());

        when(friendInterface.getAllFriends()).thenReturn(friends);

        var response = friendController.getFriends();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(friends, response.getBody());
    }

    @Test
    @DisplayName("getFriends - returns empty list")
    void getFriends_returnsEmptyList() {
        when(friendInterface.getAllFriends()).thenReturn(Collections.emptyList());

        var response = friendController.getFriends();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }

    @Test
    @DisplayName("insertFriend - success")
    void insertFriend_success() {
        FriendDTO friendDTO = FriendDTO.builder()
                .user1Id(UUID.randomUUID())
                .user2Id(UUID.randomUUID())
                .build();

        when(friendInterface.createFriend(any())).thenReturn(UUID.randomUUID());

        var response = friendController.insertFriend(friendDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Friend  inserted successfully"));
    }

    @Test
    @DisplayName("deleteFriend - success")
    void deleteFriend_success() {
        UUID id = UUID.randomUUID();

        var response = friendController.deleteFriend(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Friend  with id " + id + " has been deleted successfully"));
        verify(friendInterface).deleteFriend(id);
    }
}
