package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.FriendRequestDTO;
import com.example.disiprojectbackend.entities.FriendRequest;
import com.example.disiprojectbackend.repositories.FriendRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendRequestServiceTest {

    private FriendRequestRepository friendRequestRepository;
    private FriendRequestService friendRequestService;

    @BeforeEach
    void setUp() {
        friendRequestRepository = mock(FriendRequestRepository.class);
        friendRequestService = new FriendRequestService(friendRequestRepository, new ModelMapper());
    }

    @Test
    void createFriendRequest_success() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(UUID.randomUUID());
        FriendRequestDTO dto = FriendRequestDTO.builder().build();

        when(friendRequestRepository.save(any())).thenReturn(friendRequest);

        UUID result = friendRequestService.createFriendRequest(dto);

        assertNotNull(result);
        verify(friendRequestRepository).save(any());
    }

    @Test
    void getFriendRequestById_found() {
        UUID id = UUID.randomUUID();
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(id);

        when(friendRequestRepository.findById(id)).thenReturn(Optional.of(friendRequest));

        FriendRequestDTO result = friendRequestService.getFriendRequestById(id);

        assertNotNull(result);
    }

    @Test
    void getFriendRequestById_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(friendRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> friendRequestService.getFriendRequestById(id));
    }

    @Test
    void getAllFriendRequest_success() {
        when(friendRequestRepository.findAll()).thenReturn(List.of(new FriendRequest()));

        List<FriendRequestDTO> result = friendRequestService.getAllFriendRequest();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void updateFriendRequest_found() {
        UUID id = UUID.randomUUID();
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(id);

        FriendRequestDTO dto = FriendRequestDTO.builder()
                .id(id)
                .status("accepted")
                .build();

        when(friendRequestRepository.findById(id)).thenReturn(Optional.of(friendRequest));

        friendRequestService.updateFriendRequest(dto);

        verify(friendRequestRepository).save(friendRequest);
        assertEquals("accepted", friendRequest.getStatus());
    }

    @Test
    void deleteFriendRequest_found() {
        UUID id = UUID.randomUUID();
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(id);

        when(friendRequestRepository.findById(id)).thenReturn(Optional.of(friendRequest));

        friendRequestService.deleteFriendRequest(id);

        verify(friendRequestRepository).deleteById(id);
    }

    @Test
    void getAllFriendRequestsByReceiverId_success() {
        UUID receiverId = UUID.randomUUID();
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(UUID.randomUUID());

        when(friendRequestRepository.findAllByReceiverId(receiverId)).thenReturn(List.of(friendRequest));

        List<FriendRequestDTO> result = friendRequestService.getAllFriendRequestsByReceiverId(receiverId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllFriendRequestsByReceiverId_emptyList() {
        UUID receiverId = UUID.randomUUID();

        when(friendRequestRepository.findAllByReceiverId(receiverId)).thenReturn(Collections.emptyList());

        List<FriendRequestDTO> result = friendRequestService.getAllFriendRequestsByReceiverId(receiverId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
