package com.example.disiprojectbackend.serviceInterfaces;

import com.example.disiprojectbackend.DTOs.PhotoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PhotoServiceInterface {
    String uploadAlbumImage(UUID albumId, MultipartFile file);

    void deletePhoto(UUID photoId);

    List<PhotoDTO> getAllPhotosByAlbumId(UUID albumId);
}
