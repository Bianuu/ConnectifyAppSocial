package com.example.disiprojectbackend.repositories;

import com.example.disiprojectbackend.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findAllByUser_Id(UUID userId);

    List<Comment> findAllByPost_Id(UUID postId);
}
