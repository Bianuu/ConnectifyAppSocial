package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.PostDTO;
import com.example.disiprojectbackend.entities.Post;
import com.example.disiprojectbackend.entities.User;
import com.example.disiprojectbackend.repositories.PostRepository;
import com.example.disiprojectbackend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private PostService postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        userRepository = mock(UserRepository.class);
        postService = new PostService(postRepository, userRepository, new ModelMapper());
    }

    @Test
    void createPost_userExists_success() throws IOException {
        UUID userId = UUID.randomUUID();
        PostDTO postDTO = PostDTO.builder().content("Test content").build();
        User user = new User();
        Post post = new Post();
        post.setId(UUID.randomUUID());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        UUID result = postService.createPost(postDTO, userId, null);

        assertNotNull(result);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPost_withImage_success() throws IOException {
        UUID userId = UUID.randomUUID();
        PostDTO postDTO = PostDTO.builder().content("Test content").build();
        User user = new User();
        Post post = new Post();
        post.setId(UUID.randomUUID());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "image data".getBytes());

        UUID result = postService.createPost(postDTO, userId, file);

        assertNotNull(result);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPost_userNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        PostDTO postDTO = PostDTO.builder().content("Test").build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.createPost(postDTO, userId, null));
    }

    @Test
    void updatePost_postExists_success() {
        UUID postId = UUID.randomUUID();
        Post post = new Post();
        post.setId(postId);
        post.setContent("Old Content");

        PostDTO postDTO = PostDTO.builder().id(postId).content("New Content").build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.updatePost(postDTO);

        verify(postRepository).save(post);
        assertEquals("New Content", post.getContent());
    }

    @Test
    void deletePost_postExists_success() {
        UUID postId = UUID.randomUUID();
        Post post = new Post();
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.deletePost(postId);

        verify(postRepository).deleteById(postId);
    }

    @Test
    void deletePost_postNotFound_logsError() {
        UUID postId = UUID.randomUUID();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> postService.deletePost(postId));
    }

    @Test
    void createPost_withEmptyFile_success() throws IOException {
        UUID userId = UUID.randomUUID();
        PostDTO postDTO = PostDTO.builder().content("Test content").build();
        User user = new User();
        Post post = new Post();
        post.setId(UUID.randomUUID());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        MockMultipartFile file = new MockMultipartFile("image", "empty.jpg", "image/jpeg", new byte[0]);

        UUID result = postService.createPost(postDTO, userId, file);

        assertNotNull(result);
        verify(postRepository).save(any(Post.class));
    }
}
