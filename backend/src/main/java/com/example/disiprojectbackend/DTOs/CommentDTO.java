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
public class CommentDTO {

    private UUID id;
    private UUID userId;
    private UUID postId;
    private String content;
    private Date createdAt;
    private String username; // 🔧 ADAUGAT
    private String postAuthor; // ✅ adăugat


}
