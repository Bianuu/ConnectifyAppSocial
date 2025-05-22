package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.PostDTO;
import com.example.disiprojectbackend.serviceInterfaces.PostServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostControllerTest {

    @Mock
    private PostServiceInterface postServiceInterface;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getAllPosts - success")
    void getAllPosts_success() {
        when(postServiceInterface.getAllPosts()).thenReturn(Collections.emptyList());

        var response = postController.getAllPosts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("getAllPostsByUserId - success")
    void getAllPostsByUserId_success() {
        UUID userId = UUID.randomUUID();
        when(postServiceInterface.getAllPostsByUserId(userId)).thenReturn(Collections.emptyList());

        var response = postController.getAllPostsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("insertPost - success")
    void insertPost_success() throws IOException {
        UUID userId = UUID.randomUUID();
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test content".getBytes());

        when(postServiceInterface.createPost(any(), eq(userId), isNull())).thenReturn(UUID.randomUUID());

        var response = postController.insertPost("valid content", userId, image);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("message"));
    }

    @Test
    @DisplayName("insertPost - validation fails")
    void insertPost_validationFails() throws IOException {
        UUID userId = UUID.randomUUID();

        var response = postController.insertPost("", userId, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("deletePost - success")
    void deletePost_success() {
        UUID postId = UUID.randomUUID();

        var response = postController.deletePost(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(postServiceInterface).deletePost(postId);
    }

    @Test
    @DisplayName("updatePost - success")
    void updatePost_success() {
        PostDTO post = PostDTO.builder()
                .id(UUID.randomUUID())
                .content("Updated content")
                .build();

        var response = postController.updatePost(post);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(postServiceInterface).updatePost(post);
    }

    @Test
    @DisplayName("updatePost - validation fails")
    void updatePost_validationFails() {
        PostDTO post = PostDTO.builder()
                .id(UUID.randomUUID())
                .content("")
                .build();

        var response = postController.updatePost(post);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
