package com.example.disiprojectbackend.serviceInterfaces;

import com.example.disiprojectbackend.DTOs.CommentDTO;

import java.util.List;
import java.util.UUID;

public interface CommentInterface {

    UUID createComment(CommentDTO commentDTO);

    List<CommentDTO> getAllComments();

    List<CommentDTO> getAllCommentsByUserId(UUID userId);

    List<CommentDTO> getAllCommentsByPostId(UUID postId);

    void updateComment(CommentDTO commentDTO);

    void deleteComment(UUID id);

}
