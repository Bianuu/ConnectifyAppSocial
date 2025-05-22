package com.example.disiprojectbackend.serviceInterfaces;

import com.example.disiprojectbackend.DTOs.AlbumDTO;

import java.util.List;
import java.util.UUID;

public interface AlbumServiceInterface {
    UUID createAlbum(AlbumDTO albumDTO, UUID userId);

    List<AlbumDTO> getAllAlbumsByUserId(UUID userId);

    void updateAlbum(AlbumDTO albumDTO);

    void deleteAlbum(UUID id);
}
