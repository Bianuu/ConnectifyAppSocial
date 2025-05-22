package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.LikeDTO;
import com.example.disiprojectbackend.serviceInterfaces.LikeServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LikeControllerTest {

    @Mock
    private LikeServiceInterface likeServiceInterface;

    @InjectMocks
    private LikeController likeController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("insertLike - success")
    void insertLike_success() {
        LikeDTO likeDTO = LikeDTO.builder()
                .userId(UUID.randomUUID())
                .postId(UUID.randomUUID())
                .build();
        when(likeServiceInterface.createLike(any(), any(), any())).thenReturn(UUID.randomUUID());

        var response = likeController.insertLike(likeDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Like inserted successfully", response.getBody());
        verify(likeServiceInterface).createLike(eq(likeDTO), eq(likeDTO.getUserId()), eq(likeDTO.getPostId()));
    }

    @Test
    @DisplayName("deleteLike - success")
    void deleteLike_success() {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        var response = likeController.deleteLike(postId, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Like deleted successfully", response.getBody());
        verify(likeServiceInterface).deleteLike(postId, userId);
    }
}
