package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.PhotoDTO;
import com.example.disiprojectbackend.serviceInterfaces.PhotoServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PhotoControllerTest {

    @Mock
    private PhotoServiceInterface photoServiceInterface;

    @InjectMocks
    private PhotoController photoController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("uploadImageToAlbum - success")
    void uploadImageToAlbum_success() {
        UUID albumId = UUID.randomUUID();
        MultipartFile file = new MockMultipartFile("image", "photo.jpg", "image/jpeg", "content".getBytes());
        when(photoServiceInterface.uploadAlbumImage(eq(albumId), any())).thenReturn("Photo uploaded successfully");

        var response = photoController.uploadImageToAlbum(albumId, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Photo uploaded successfully", response.getBody());
    }

    @Test
    @DisplayName("uploadImageToAlbum - failure response")
    void uploadImageToAlbum_failure() {
        UUID albumId = UUID.randomUUID();
        MultipartFile file = new MockMultipartFile("image", "photo.jpg", "image/jpeg", "content".getBytes());
        when(photoServiceInterface.uploadAlbumImage(eq(albumId), any())).thenReturn("Upload failed");

        var response = photoController.uploadImageToAlbum(albumId, file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Upload failed", response.getBody());
    }

    @Test
    @DisplayName("uploadImageToAlbum - empty file")
    void uploadImageToAlbum_emptyFile() {
        UUID albumId = UUID.randomUUID();
        MultipartFile file = new MockMultipartFile("image", "photo.jpg", "image/jpeg", new byte[0]);
        when(photoServiceInterface.uploadAlbumImage(eq(albumId), any())).thenReturn("Photo uploaded successfully");

        var response = photoController.uploadImageToAlbum(albumId, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Photo uploaded successfully", response.getBody());
    }

    @Test
    @DisplayName("uploadImageToAlbum - exception in service")
    void uploadImageToAlbum_exception() {
        UUID albumId = UUID.randomUUID();
        MultipartFile file = new MockMultipartFile("image", "photo.jpg", "image/jpeg", "content".getBytes());
        when(photoServiceInterface.uploadAlbumImage(eq(albumId), any())).thenThrow(new RuntimeException("Service error"));

        assertThrows(RuntimeException.class, () -> photoController.uploadImageToAlbum(albumId, file));
    }

    @Test
    @DisplayName("deletePhoto - success")
    void deletePhoto_success() {
        UUID photoId = UUID.randomUUID();

        var response = photoController.deletePhoto(photoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(photoServiceInterface).deletePhoto(photoId);
    }

    @Test
    @DisplayName("getAllPhotosByAlbumId - success with photos")
    void getAllPhotosByAlbumId_success() {
        UUID albumId = UUID.randomUUID();
        PhotoDTO photo = PhotoDTO.builder()
                .id(UUID.randomUUID())
                .albumId(albumId)
                .image(new byte[]{1, 2, 3})
                .build();
        when(photoServiceInterface.getAllPhotosByAlbumId(albumId)).thenReturn(List.of(photo));

        var response = photoController.getAllPhotosByAlbumId(albumId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PhotoDTO> photos = (List<PhotoDTO>) response.getBody();
        assertNotNull(photos);
        assertFalse(photos.isEmpty());
        assertEquals(albumId, photos.get(0).getAlbumId());
    }

    @Test
    @DisplayName("getAllPhotosByAlbumId - no photos")
    void getAllPhotosByAlbumId_noPhotos() {
        UUID albumId = UUID.randomUUID();
        when(photoServiceInterface.getAllPhotosByAlbumId(albumId)).thenReturn(Collections.emptyList());

        var response = photoController.getAllPhotosByAlbumId(albumId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PhotoDTO> photos = (List<PhotoDTO>) response.getBody();
        assertNotNull(photos);
        assertTrue(photos.isEmpty());
    }
}
