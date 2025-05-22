package com.example.disiprojectbackend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private UUID id;
    private UUID userId;
    private String content;
    private byte[] image;
    private int likesCount;
    private Date createdAt;
    private Date updatedAt;
    private String postAuthor;

}
