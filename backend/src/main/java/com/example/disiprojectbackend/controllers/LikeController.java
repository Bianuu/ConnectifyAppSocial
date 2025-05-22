package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.LikeDTO;
import com.example.disiprojectbackend.serviceInterfaces.LikeServiceInterface;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/likes")
public class LikeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LikeController.class);
    private final LikeServiceInterface likeServiceInterface;

    @DeleteMapping("/delete/{postId}/{userId}")
    public ResponseEntity<?> deleteLike(@PathVariable("postId") UUID postId, @PathVariable("userId") UUID userId) {
        likeServiceInterface.deleteLike(postId, userId);
        LOGGER.info("Like for post {} by user {} was deleted", postId, userId);
        return ResponseEntity.ok("Like deleted successfully");
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertLike(@RequestBody LikeDTO likeDTO) {
        UUID insertedId = likeServiceInterface.createLike(likeDTO, likeDTO.getUserId(), likeDTO.getPostId());
        LOGGER.info("Like with id " + insertedId + " was inserted in the database");
        return ResponseEntity.ok("Like inserted successfully");
    }

    @GetMapping("/user/{userId}/post/{postId}")
    public ResponseEntity<Boolean> hasUserLikedPost(@PathVariable UUID userId, @PathVariable UUID postId) {
        boolean exists = likeServiceInterface.hasUserLikedPost(userId, postId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/count/{postId}")
    public ResponseEntity<Long> getLikeCountForPost(@PathVariable UUID postId) {
        long count = likeServiceInterface.countLikesForPost(postId);
        return ResponseEntity.ok(count);
    }

}
