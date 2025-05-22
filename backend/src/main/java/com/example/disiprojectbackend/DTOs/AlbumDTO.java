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
public class AlbumDTO {

    private UUID id;
    private UUID userId;
    private String name;
    private int numberOfPhotos;
    private Date createdAt;
}
