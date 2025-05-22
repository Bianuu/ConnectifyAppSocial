package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.FriendRequestDTO;
import com.example.disiprojectbackend.serviceInterfaces.FriendRequestInterface;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FriendRequestControllerTest {

    @Mock
    private FriendRequestInterface friendRequestInterface;

    @InjectMocks
    private FriendRequestController friendRequestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getFriendRequests - returns list")
    void getFriendRequests_returnsList() {
        when(friendRequestInterface.getAllFriendRequest()).thenReturn(
                List.of(new FriendRequestDTO())
        );

        var response = friendRequestController.getFriendRequests();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
    }

    @Test
    @DisplayName("getFriendRequests - returns empty list")
    void getFriendRequests_returnsEmpty() {
        when(friendRequestInterface.getAllFriendRequest()).thenReturn(Collections.emptyList());

        var response = friendRequestController.getFriendRequests();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }

    @Test
    @DisplayName("insertFriendRequest - success")
    void insertFriendRequest_success() {
        FriendRequestDTO request = FriendRequestDTO.builder()
                .senderId(UUID.randomUUID())
                .receiverId(UUID.randomUUID())
                .build();

        when(friendRequestInterface.createFriendRequest(any())).thenReturn(UUID.randomUUID());

        var response = friendRequestController.insertFriendRequest(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Friend request inserted successfully"));
        verify(friendRequestInterface).createFriendRequest(request);
    }

    @Test
    @DisplayName("insertFriendRequest - edge case: null request")
    void insertFriendRequest_nullRequest() {
        assertThrows(NullPointerException.class, () -> friendRequestController.insertFriendRequest(null));
    }

    @Test
    @DisplayName("updateFriendRequest - success")
    void updateFriendRequest_success() {
        FriendRequestDTO request = FriendRequestDTO.builder()
                .id(UUID.randomUUID())
                .senderId(UUID.randomUUID())
                .receiverId(UUID.randomUUID())
                .build();

        var response = friendRequestController.updateFriendRequest(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("User with id"));
        verify(friendRequestInterface).updateFriendRequest(request);
    }

    @Test
    @DisplayName("updateFriendRequest - edge case: null request")
    void updateFriendRequest_nullRequest() {
        assertThrows(NullPointerException.class, () -> friendRequestController.updateFriendRequest(null));
    }

    @Test
    @DisplayName("deleteFriendRequest - success")
    void deleteFriendRequest_success() {
        UUID id = UUID.randomUUID();

        var response = friendRequestController.deleteFriendRequest(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Friend request with id"));
        verify(friendRequestInterface).deleteFriendRequest(id);
    }

    @Test
    @DisplayName("getAllFriendRequestsBySenderId - success")
    void getAllFriendRequestsBySenderId_success() {
        UUID senderId = UUID.randomUUID();
        when(friendRequestInterface.getAllFriendRequestsByReceiverId(senderId)).thenReturn(
                List.of(new FriendRequestDTO())
        );

        var response = friendRequestController.getAllFriendRequestsBySenderId(senderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
    }

    @Test
    @DisplayName("getAllFriendRequestsBySenderId - edge case: no requests")
    void getAllFriendRequestsBySenderId_noRequests() {
        UUID senderId = UUID.randomUUID();
        when(friendRequestInterface.getAllFriendRequestsByReceiverId(senderId)).thenReturn(Collections.emptyList());

        var response = friendRequestController.getAllFriendRequestsBySenderId(senderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }
}
