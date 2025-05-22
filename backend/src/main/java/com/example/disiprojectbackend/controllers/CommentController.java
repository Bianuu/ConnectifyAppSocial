package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.CommentDTO;
import com.example.disiprojectbackend.serviceInterfaces.CommentInterface;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Controller
@AllArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);
    private final CommentInterface commentInterface;

    @PostMapping("/insert")
    public ResponseEntity<?> insertComment(@RequestBody CommentDTO commentDTO) {
        UUID insertedId = commentInterface.createComment(commentDTO);
        LOGGER.info("Comment with id " + insertedId + " was inserted in the database");
        return ResponseEntity.ok("Comment inserted successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") UUID id) {
        commentInterface.deleteComment(id);
        LOGGER.info("Comment with id {} was deleted", id);
        return ResponseEntity.ok("Comment with id " + id + " has been deleted successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateComment(@RequestBody CommentDTO commentDTO) {
        commentInterface.updateComment(commentDTO);
        LOGGER.info("Comment with id " + commentDTO.getId() + " was updated in the database");
        return ResponseEntity.ok("Comment updated successfully");
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getAllAlbumsByUserId(@PathVariable("userId") UUID userId) {
        LOGGER.info("Getting all comments for user with id {}", userId);
        List<CommentDTO> commentDTOS = null;
        commentDTOS = commentInterface.getAllCommentsByUserId(userId);
        return ResponseEntity.ok(commentDTOS);
    }

    @GetMapping("/allP/{postId}")
    public ResponseEntity<?> getAllAlbumsByPostId(@PathVariable("postId") UUID postId) {
        LOGGER.info("Getting all comments for post with id {}", postId);
        List<CommentDTO> commentDTOS = null;
        commentDTOS = commentInterface.getAllCommentsByPostId(postId);
        return ResponseEntity.ok(commentDTOS);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getComments() {
        LOGGER.info("Getting all comments");
        List<CommentDTO> commentDTOS = null;

        commentDTOS = commentInterface.getAllComments();


        return ResponseEntity.ok(commentDTOS);
    }
}
