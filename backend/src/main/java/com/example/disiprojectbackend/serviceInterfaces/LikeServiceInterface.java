package com.example.disiprojectbackend.serviceInterfaces;

import com.example.disiprojectbackend.DTOs.LikeDTO;

import java.util.UUID;

public interface LikeServiceInterface {
    UUID createLike(LikeDTO likeDTO, UUID userId, UUID postId);

    void deleteLike(UUID userId, UUID postId);

    boolean hasUserLikedPost(UUID userId, UUID postId);

    long countLikesForPost(UUID postId);
}
