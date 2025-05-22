package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.LikeDTO;
import com.example.disiprojectbackend.DTOs.builders.LikeBuilder;
import com.example.disiprojectbackend.entities.Like;
import com.example.disiprojectbackend.entities.Post;
import com.example.disiprojectbackend.entities.User;
import com.example.disiprojectbackend.repositories.LikeRepository;
import com.example.disiprojectbackend.repositories.PostRepository;
import com.example.disiprojectbackend.repositories.UserRepository;
import com.example.disiprojectbackend.serviceInterfaces.LikeServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Builder
@AllArgsConstructor
public class LikeService implements LikeServiceInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(LikeService.class);
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private ModelMapper modelMapper;

    @Override
    public UUID createLike(LikeDTO likeDTO, UUID userId, UUID postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
        Like newLike = LikeBuilder.toEntity(likeDTO);
        newLike.setUser(user);
        newLike.setPost(post);
        newLike.setCreatedAt(new Date());
        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.save(post);
        newLike = likeRepository.save(newLike);
        LOGGER.info("Like with id {} was inserted in db", newLike.getId());
        return newLike.getId();
    }

    @Override
    public void deleteLike(UUID postId, UUID userId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (!postOptional.isPresent()) {
            LOGGER.error("Post with id {} was not found", postId);
            throw new EntityNotFoundException("Post not found");
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found", userId);
            throw new EntityNotFoundException("User not found");
        }

        Optional<Like> likeOptional = likeRepository.findByUserIdAndPostId(userId, postId);
        if (!likeOptional.isPresent()) {
            LOGGER.error("Like not found for user with id {} and post with id {}", userId, postId);
            throw new EntityNotFoundException("Like not found");
        }

        Like like = likeOptional.get();
        UUID likeId = like.getId();
        likeRepository.deleteById(likeId);
        postOptional.get().setLikesCount(postOptional.get().getLikesCount() - 1);
        postRepository.save(postOptional.get());
        LOGGER.info("Like with id {} was deleted from db", likeId);
    }

    @Override
    @Transactional
    public boolean hasUserLikedPost(UUID userId, UUID postId) {
        return likeRepository.findByUserIdAndPostId(userId, postId).isPresent();
    }

    @Override
    public long countLikesForPost(UUID postId) {
        return likeRepository.countByPostId(postId);
    }

}
