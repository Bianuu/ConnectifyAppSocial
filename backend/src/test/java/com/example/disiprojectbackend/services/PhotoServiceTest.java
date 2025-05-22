package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.PhotoDTO;
import com.example.disiprojectbackend.entities.Album;
import com.example.disiprojectbackend.entities.Photo;
import com.example.disiprojectbackend.repositories.AlbumRepository;
import com.example.disiprojectbackend.repositories.PhotoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PhotoServiceTest {

    private AlbumRepository albumRepository;
    private PhotoRepository photoRepository;
    private PhotoService photoService;

    @BeforeEach
    void setUp() {
        albumRepository = mock(AlbumRepository.class);
        photoRepository = mock(PhotoRepository.class);
        photoService = new PhotoService(new ModelMapper(), albumRepository, photoRepository);
    }

    @Test
    void uploadAlbumImage_albumExists_success() throws IOException {
        UUID albumId = UUID.randomUUID();
        Album album = new Album();
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test data".getBytes());

        String result = photoService.uploadAlbumImage(albumId, file);

        assertEquals("Photo uploaded successfully", result);
        verify(photoRepository).save(any(Photo.class));
    }

    @Test
    void uploadAlbumImage_albumNotFound_returnsError() throws IOException {
        UUID albumId = UUID.randomUUID();
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test data".getBytes());

        String result = photoService.uploadAlbumImage(albumId, file);

        assertEquals("Album not found", result);
        verify(photoRepository, never()).save(any(Photo.class));
    }

    @Test
    void deletePhoto_photoExists_success() {
        UUID photoId = UUID.randomUUID();
        Photo photo = new Photo();
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

        photoService.deletePhoto(photoId);

        verify(photoRepository).delete(photo);
    }

    @Test
    void deletePhoto_photoNotFound_throwsException() {
        UUID photoId = UUID.randomUUID();
        when(photoRepository.findById(photoId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> photoService.deletePhoto(photoId));
    }

    @Test
    void getAllPhotosByAlbumId_photosExist_returnsList() {
        UUID albumId = UUID.randomUUID();
        Photo photo = new Photo();
        when(photoRepository.findAllByAlbumId(albumId)).thenReturn(List.of(photo));

        List<PhotoDTO> result = photoService.getAllPhotosByAlbumId(albumId);

        assertEquals(1, result.size());
    }

    @Test
    void getAllPhotosByAlbumId_noPhotos_throwsException() {
        UUID albumId = UUID.randomUUID();
        when(photoRepository.findAllByAlbumId(albumId)).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, () -> photoService.getAllPhotosByAlbumId(albumId));
    }

    @Test
    void uploadAlbumImage_emptyFile_success() throws IOException {
        UUID albumId = UUID.randomUUID();
        Album album = new Album();
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        MockMultipartFile file = new MockMultipartFile("image", "empty.jpg", "image/jpeg", new byte[0]);

        String result = photoService.uploadAlbumImage(albumId, file);

        assertEquals("Photo uploaded successfully", result);
        verify(photoRepository).save(any(Photo.class));
    }
}
