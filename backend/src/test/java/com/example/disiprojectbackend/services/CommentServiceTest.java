package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.CommentDTO;
import com.example.disiprojectbackend.entities.Comment;
import com.example.disiprojectbackend.entities.Post;
import com.example.disiprojectbackend.entities.User;
import com.example.disiprojectbackend.repositories.CommentRepository;
import com.example.disiprojectbackend.repositories.PostRepository;
import com.example.disiprojectbackend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private PostRepository postRepository;
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        userRepository = mock(UserRepository.class);
        postRepository = mock(PostRepository.class);

        commentService = new CommentService(commentRepository, userRepository, postRepository);
    }

    @Test
    void createComment_success() {
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        User user = new User();
        Post post = new Post();
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());

        CommentDTO dto = CommentDTO.builder()
                .userId(userId)
                .postId(postId)
                .content("Test comment")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(any())).thenReturn(comment);

        UUID result = commentService.createComment(dto);

        assertNotNull(result);
    }

    @Test
    void createComment_userNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        CommentDTO dto = CommentDTO.builder()
                .userId(userId)
                .postId(UUID.randomUUID())
                .content("Test comment")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.createComment(dto));
    }

    @Test
    void createComment_postNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        CommentDTO dto = CommentDTO.builder()
                .userId(userId)
                .postId(postId)
                .content("Test comment")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.createComment(dto));
    }

    @Test
    void getAllComments_success() {
        when(commentRepository.findAll()).thenReturn(List.of(new Comment()));

        List<CommentDTO> result = commentService.getAllComments();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllCommentsByUserId_success() {
        UUID userId = UUID.randomUUID();
        when(commentRepository.findAllByUser_Id(userId)).thenReturn(List.of(new Comment()));

        List<CommentDTO> result = commentService.getAllCommentsByUserId(userId);

        assertNotNull(result);
    }

    @Test
    void getAllCommentsByUserId_noComments_throwsException() {
        UUID userId = UUID.randomUUID();
        when(commentRepository.findAllByUser_Id(userId)).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, () -> commentService.getAllCommentsByUserId(userId));
    }

    @Test
    void getAllCommentsByPostId_noComments_throwsException() {
        UUID postId = UUID.randomUUID();
        when(commentRepository.findAllByUser_Id(postId)).thenReturn(Collections.emptyList()); // după codul tău

        assertThrows(EntityNotFoundException.class, () -> commentService.getAllCommentsByPostId(postId));
    }

    @Test
    void updateComment_success() {
        UUID commentId = UUID.randomUUID();
        Comment comment = new Comment();
        comment.setId(commentId);

        CommentDTO dto = CommentDTO.builder()
                .id(commentId)
                .content("Updated content")
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.updateComment(dto);

        verify(commentRepository).save(comment);
        assertEquals("Updated content", comment.getContent());
    }

    @Test
    void updateComment_notFound_throwsException() {
        UUID commentId = UUID.randomUUID();
        CommentDTO dto = CommentDTO.builder()
                .id(commentId)
                .content("Updated content")
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.updateComment(dto));
    }

    @Test
    void deleteComment_success() {
        UUID commentId = UUID.randomUUID();
        Comment comment = new Comment();
        comment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(commentId);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_notFound_throwsException() {
        UUID commentId = UUID.randomUUID();
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(commentId));
    }
}
