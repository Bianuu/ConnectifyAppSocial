package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.PostDTO;
import com.example.disiprojectbackend.DTOs.builders.PostBuilder;
import com.example.disiprojectbackend.entities.Post;
import com.example.disiprojectbackend.entities.User;
import com.example.disiprojectbackend.repositories.PostRepository;
import com.example.disiprojectbackend.repositories.UserRepository;
import com.example.disiprojectbackend.serviceInterfaces.PostServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Builder
@AllArgsConstructor
@Transactional
public class PostService implements PostServiceInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private ModelMapper modelMapper;

    @Override
    public UUID createPost(PostDTO postDTO, UUID userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Post newPost = PostBuilder.toEntity(postDTO);
        newPost.setUser(user);

        try {
            if (file != null && !file.isEmpty()) {
                newPost.setImage(file.getBytes());
            }
        } catch (IOException e) {
            LOGGER.error("Error reading image bytes for post creation", e);
            throw new RuntimeException("Could not read image data", e); // or handle as you prefer
        }
        newPost.setLikesCount(0);
        newPost.setCreatedAt(new Date());
        newPost.setUpdatedAt(new Date());
        newPost = postRepository.save(newPost);
        LOGGER.info("Post with id {} was inserted in db", newPost.getId());
        return newPost.getId();
    }


    @Override
    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(PostBuilder::toPostDTO).collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> getAllPostsByUserId(UUID userId) {
        List<Post> posts = postRepository.findAllByUser_Id(userId);
        if (posts.isEmpty()) {
            LOGGER.info("User with id {} has no posts", userId); // Folosește info, nu error
            return Collections.emptyList();
        }
        return posts.stream().map(PostBuilder::toPostDTO).collect(Collectors.toList());
    }


//    @Override
//    public List<PostDTO> getAllPostsByUserId(UUID userId) {
//        List<Post> posts = postRepository.findAllByUser_Id(userId);
//        if (posts.isEmpty()) {
//            LOGGER.error("User with id {} has no posts", userId);
//            throw new EntityNotFoundException("User has no posts");
//        }
//        return posts.stream().map(PostBuilder::toPostDTO).collect(Collectors.toList());
//    }

    @Override
    public void updatePost(PostDTO postDTO) {
        Optional<Post> postOptional = postRepository.findById(postDTO.getId());
        if (!postOptional.isPresent()) {

            LOGGER.info("Post with id {} was not found", postDTO.getId());
        }
        Post post = postOptional.get();
        post.setContent(postDTO.getContent());
        post.setUpdatedAt(new Date());
        postRepository.save(post);
        LOGGER.info("Post with id {} was updated", post.getId());
    }

    @Override
    public void deletePost(UUID id) {
        Optional<Post> post = postRepository.findById(id);
        if (!post.isPresent()) {
            LOGGER.error("Post with id {} was not found in db", id);
        }
        postRepository.deleteById(id);
        LOGGER.info("Post with id {} was deleted from db", id);
    }

    @Override
    public String uploadPostImage(MultipartFile file) {
        return null;
    }
}
