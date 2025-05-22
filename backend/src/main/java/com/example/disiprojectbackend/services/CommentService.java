package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.CommentDTO;
import com.example.disiprojectbackend.DTOs.builders.CommentBuilder;
import com.example.disiprojectbackend.entities.Comment;
import com.example.disiprojectbackend.entities.Post;
import com.example.disiprojectbackend.entities.User;
import com.example.disiprojectbackend.repositories.CommentRepository;
import com.example.disiprojectbackend.repositories.PostRepository;
import com.example.disiprojectbackend.repositories.UserRepository;
import com.example.disiprojectbackend.serviceInterfaces.CommentInterface;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Builder
@AllArgsConstructor
public class CommentService implements CommentInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public UUID createComment(CommentDTO commentDTO) {
        User user = userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id:" + commentDTO.getUserId()));
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id:" + commentDTO.getPostId()));

        Comment comment = CommentBuilder.toEntity(commentDTO, user, post);
        comment = commentRepository.save(comment);
        return comment.getId();

    }

    @Override
    public List<CommentDTO> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream().map(CommentBuilder::toCommentDTO).collect(Collectors.toList());

    }

    @Override
    public List<CommentDTO> getAllCommentsByUserId(UUID userId) {
        List<Comment> comments = commentRepository.findAllByUser_Id(userId);
        if (comments.isEmpty()) {
            LOGGER.error("User with id {} has no comments", userId);
            throw new EntityNotFoundException("User has no comments");
        }
        return comments.stream().map(CommentBuilder::toCommentDTO).collect(Collectors.toList());

    }


    @Transactional
    @Override
    public List<CommentDTO> getAllCommentsByPostId(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    LOGGER.error("Post with id {} was not found", postId);
                    return new EntityNotFoundException("Post not found with id: " + postId);
                });

        List<Comment> comments = commentRepository.findAllByPost_Id(postId);

        return comments.stream()
                .map(CommentBuilder::toCommentDTO)
                .collect(Collectors.toList());
    }


    @Override
    public void updateComment(CommentDTO commentDTO) {

        Optional<Comment> commentOptional = commentRepository.findById(commentDTO.getId());
        if (!commentOptional.isPresent()) {
            LOGGER.error("Comment with id {} was not found", commentDTO.getId());
            throw new EntityNotFoundException("Comment not found");
        }
        Comment comment = commentOptional.get();
        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);
        LOGGER.info("Comment with id {} was updated", comment.getId());

    }

    @Override
    public void deleteComment(UUID id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (!commentOptional.isPresent()) {
            LOGGER.error("Comment with id {} was not found", id);
            throw new EntityNotFoundException("Comment not found");
        }
        Comment comment = commentOptional.get();
        commentRepository.delete(comment);
        LOGGER.info("Comment with id {} was deleted", id);

    }
}
