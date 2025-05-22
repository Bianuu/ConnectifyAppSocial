package com.example.disiprojectbackend.serviceInterfaces;

import com.example.disiprojectbackend.DTOs.PostDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PostServiceInterface {

    UUID createPost(PostDTO postDTO, UUID userId, MultipartFile file);

    List<PostDTO> getAllPosts();

    List<PostDTO> getAllPostsByUserId(UUID userId);

    void updatePost(PostDTO postDTO);

    void deletePost(UUID id);

    String uploadPostImage(MultipartFile file);
}
