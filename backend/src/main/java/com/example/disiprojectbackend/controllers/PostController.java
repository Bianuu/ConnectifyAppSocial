package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.PostDTO;
import com.example.disiprojectbackend.serviceInterfaces.PostServiceInterface;
import com.example.disiprojectbackend.validators.PostValidator;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final PostServiceInterface postServiceInterface;

    @GetMapping("/all")
    public ResponseEntity<?> getAllPosts() {
        LOGGER.info("Getting all posts");
        List<PostDTO> posts = null;
        posts = postServiceInterface.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getAllPostsByUserId(@PathVariable("userId") UUID userId) {
        LOGGER.info("Getting all posts for user with id {}", userId);
        List<PostDTO> posts = null;
        posts = postServiceInterface.getAllPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertPost(
            @RequestParam("content") String content,
            @RequestParam("userId") UUID userId,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        // Validate the content
        List<String> validationErrors = PostValidator.validateWholeDataForPost(content);

        if (!validationErrors.isEmpty()) {
            Map<String, Object> response = Map.of(
                    "error", "Validation failed",
                    "details", validationErrors
            );
            LOGGER.info("Invalid post data: " + validationErrors);
            return ResponseEntity.badRequest().body(response);
        }

        // Create a PostDTO using the request data
        PostDTO postDTO = PostDTO.builder()
                .content(content)
                .userId(userId)
                .image(image != null ? image.getBytes() : null)  // Convert image to byte[] if provided
                .likesCount(0)  // You can initialize likesCount as needed
                .createdAt(new Date())  // Set current time for createdAt
                .updatedAt(new Date())  // Set current time for updatedAt
                .build();

        // Call your service method to save the post
        UUID insertedId = postServiceInterface.createPost(postDTO, userId, null);

        LOGGER.info("Post with id " + insertedId + " created successfully");

        Map<String, Object> response = Map.of(
                "message", "Post created successfully",
                "postId", insertedId.toString()
        );

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") UUID id) {
        postServiceInterface.deletePost(id);
        LOGGER.info("Post with id {} was deleted", id);
        return ResponseEntity.ok("Post with id " + id + " has been deleted successfully");
    }


    @PutMapping("/update")
    public ResponseEntity<?> updatePost(@RequestBody PostDTO postDTO) {

        List<String> validationErrors = PostValidator.validateWholeDataForPost(
                postDTO.getContent());

        if (!validationErrors.isEmpty()) {
            Map<String, Object> response = Map.of(
                    "error", "Validation failed",
                    "details", validationErrors
            );
            LOGGER.info("Datele introduse nu sunt corecte: " + validationErrors);
            return ResponseEntity.badRequest().body(response);
        }

        postServiceInterface.updatePost(postDTO);
        LOGGER.info("Post with id {} was updated", postDTO.getId());

        Map<String, String> response = Map.of(
                "message", "Post with id " + postDTO.getId() + " has been updated successfully"
        );

        return ResponseEntity.ok(response);
    }

}
