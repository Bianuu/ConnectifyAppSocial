package com.example.disiprojectbackend.DTOs.builders;

import com.example.disiprojectbackend.DTOs.CommentDTO;
import com.example.disiprojectbackend.entities.Comment;
import com.example.disiprojectbackend.entities.Post;
import com.example.disiprojectbackend.entities.User;

public class CommentBuilder {

    // CommentBuilder.java
    // CommentBuilder.java
    public static CommentDTO toCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .userId(comment.getUser() != null ? comment.getUser().getId() : null)
                .postId(comment.getPost() != null ? comment.getPost().getId() : null)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .username(comment.getUser() != null ? comment.getUser().getUsername2() : null)
                .postAuthor(comment.getPost() != null && comment.getPost().getUser() != null
                        ? comment.getPost().getUser().getUsername2() : null) // ✅
                .build();
    }


    public static Comment toEntity(CommentDTO dto, User user, Post post) {
        return Comment.builder()
                .id(dto.getId())
                .user(user)
                .post(post)
                .content(dto.getContent())
                .createdAt(dto.getCreatedAt())
                .build();
    }


}
