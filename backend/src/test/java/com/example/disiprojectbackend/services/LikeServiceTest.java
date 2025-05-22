package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.LikeDTO;
import com.example.disiprojectbackend.entities.Like;
import com.example.disiprojectbackend.entities.Post;
import com.example.disiprojectbackend.entities.User;
import com.example.disiprojectbackend.repositories.LikeRepository;
import com.example.disiprojectbackend.repositories.PostRepository;
import com.example.disiprojectbackend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LikeServiceTest {

    private LikeRepository likeRepository;
    private PostRepository postRepository;
    private UserRepository userRepository;
    private LikeService likeService;

    @BeforeEach
    void setUp() {
        likeRepository = mock(LikeRepository.class);
        postRepository = mock(PostRepository.class);
        userRepository = mock(UserRepository.class);

        likeService = new LikeService(
                new ModelMapper(),
                likeRepository,
                postRepository,
                userRepository
        );
    }

    @Test
    void createLike_success() {
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        LikeDTO likeDTO = LikeDTO.builder().build();

        User user = new User();
        user.setId(userId);
        Post post = new Post();
        post.setId(postId);
        post.setLikesCount(0);

        Like like = new Like();
        like.setId(UUID.randomUUID());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.save(any())).thenReturn(like);

        UUID result = likeService.createLike(likeDTO, userId, postId);

        assertNotNull(result);
        assertEquals(1, post.getLikesCount());
        verify(postRepository).save(post);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void createLike_userNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> likeService.createLike(new LikeDTO(), userId, postId));
    }

    @Test
    void createLike_postNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> likeService.createLike(new LikeDTO(), userId, postId));
    }

    @Test
    void deleteLike_success() {
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID likeId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        Post post = new Post();
        post.setId(postId);
        post.setLikesCount(5);

        Like like = new Like();
        like.setId(likeId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(likeRepository.findByUserIdAndPostId(userId, postId)).thenReturn(Optional.of(like));

        likeService.deleteLike(postId, userId);

        assertEquals(4, post.getLikesCount());
        verify(likeRepository).deleteById(likeId);
        verify(postRepository).save(post);
    }

    @Test
    void deleteLike_postNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> likeService.deleteLike(userId, postId));
    }

    @Test
    void deleteLike_userNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        when(postRepository.findById(postId)).thenReturn(Optional.of(new Post()));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> likeService.deleteLike(userId, postId));
    }

    @Test
    void deleteLike_likeNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();

        when(postRepository.findById(postId)).thenReturn(Optional.of(new Post()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(likeRepository.findByUserIdAndPostId(userId, postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> likeService.deleteLike(userId, postId));
    }
}
