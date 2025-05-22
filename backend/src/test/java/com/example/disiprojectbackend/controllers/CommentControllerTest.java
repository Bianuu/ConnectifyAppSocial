package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.CommentDTO;
import com.example.disiprojectbackend.serviceInterfaces.CommentInterface;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommentControllerTest {

    @Mock
    private CommentInterface commentInterface;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("insertComment - success")
    void insertComment_success() {
        CommentDTO comment = CommentDTO.builder().content("Test").build();
        when(commentInterface.createComment(comment)).thenReturn(UUID.randomUUID());

        var response = commentController.insertComment(comment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Comment inserted successfully", response.getBody());
        verify(commentInterface).createComment(comment);
    }

    @Test
    @DisplayName("deleteComment - success")
    void deleteComment_success() {
        UUID id = UUID.randomUUID();

        var response = commentController.deleteComment(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Comment with id " + id));
        verify(commentInterface).deleteComment(id);
    }

    @Test
    @DisplayName("updateComment - success")
    void updateComment_success() {
        CommentDTO comment = CommentDTO.builder().id(UUID.randomUUID()).content("Updated").build();

        var response = commentController.updateComment(comment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Comment updated successfully", response.getBody());
        verify(commentInterface).updateComment(comment);
    }

    @Test
    @DisplayName("updateComment - null comment")
    void updateComment_null() {
        assertThrows(NullPointerException.class, () -> commentController.updateComment(null));
    }

    @Test
    @DisplayName("getAllCommentsByUserId - returns list")
    void getAllCommentsByUserId_returnsList() {
        UUID userId = UUID.randomUUID();
        List<CommentDTO> comments = List.of(CommentDTO.builder().id(UUID.randomUUID()).build());
        when(commentInterface.getAllCommentsByUserId(userId)).thenReturn(comments);

        var response = commentController.getAllAlbumsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comments, response.getBody());
    }

    @Test
    @DisplayName("getAllCommentsByUserId - returns empty list")
    void getAllCommentsByUserId_returnsEmpty() {
        UUID userId = UUID.randomUUID();
        when(commentInterface.getAllCommentsByUserId(userId)).thenReturn(Collections.emptyList());

        var response = commentController.getAllAlbumsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }

    @Test
    @DisplayName("getAllCommentsByPostId - returns list")
    void getAllCommentsByPostId_returnsList() {
        UUID postId = UUID.randomUUID();
        List<CommentDTO> comments = List.of(CommentDTO.builder().id(UUID.randomUUID()).build());
        when(commentInterface.getAllCommentsByPostId(postId)).thenReturn(comments);

        var response = commentController.getAllAlbumsByPostId(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comments, response.getBody());
    }

    @Test
    @DisplayName("getAllCommentsByPostId - returns empty list")
    void getAllCommentsByPostId_returnsEmpty() {
        UUID postId = UUID.randomUUID();
        when(commentInterface.getAllCommentsByPostId(postId)).thenReturn(Collections.emptyList());

        var response = commentController.getAllAlbumsByPostId(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }

    @Test
    @DisplayName("getComments - returns list")
    void getComments_returnsList() {
        List<CommentDTO> comments = List.of(CommentDTO.builder().id(UUID.randomUUID()).build());
        when(commentInterface.getAllComments()).thenReturn(comments);

        var response = commentController.getComments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comments, response.getBody());
    }

    @Test
    @DisplayName("getComments - returns empty list")
    void getComments_returnsEmpty() {
        when(commentInterface.getAllComments()).thenReturn(Collections.emptyList());

        var response = commentController.getComments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }
}
