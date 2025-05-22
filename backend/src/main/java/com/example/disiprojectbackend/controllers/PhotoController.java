package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.PhotoDTO;
import com.example.disiprojectbackend.serviceInterfaces.PhotoServiceInterface;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/photos")
public class PhotoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoController.class);
    private final PhotoServiceInterface photoServiceInterface;

    @PostMapping("/{albumId}/uploadImage")
    public ResponseEntity<String> uploadImageToAlbum(
            @PathVariable UUID albumId,
            @RequestParam("image") MultipartFile file) {

        String responseMessage = photoServiceInterface.uploadAlbumImage(albumId, file);

        if (responseMessage.equals("Photo uploaded successfully")) {
            return ResponseEntity.ok(responseMessage);
        } else {
            return ResponseEntity.badRequest().body(responseMessage);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePhoto(@PathVariable("id") UUID id) {
        photoServiceInterface.deletePhoto(id);
        LOGGER.info("Photo with id {} was deleted", id);
        return ResponseEntity.ok("Photo with id " + id + " has been deleted successfully");
    }

    @GetMapping("/all/{albumId}")
    public ResponseEntity<?> getAllPhotosByAlbumId(@PathVariable("albumId") UUID albumId) {
        LOGGER.info("Getting all photos for album with id {}", albumId);
        List<PhotoDTO> photos = null;
        photos = photoServiceInterface.getAllPhotosByAlbumId(albumId);
        return ResponseEntity.ok(photos);
    }

}
