package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.AlbumDTO;
import com.example.disiprojectbackend.entities.Album;
import com.example.disiprojectbackend.entities.User;
import com.example.disiprojectbackend.repositories.AlbumRepository;
import com.example.disiprojectbackend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlbumServiceTest {

    private AlbumRepository albumRepository;
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private AlbumService albumService;

    @BeforeEach
    void setUp() {
        albumRepository = mock(AlbumRepository.class);
        userRepository = mock(UserRepository.class);
        modelMapper = new ModelMapper();

        albumService = new AlbumService(modelMapper, albumRepository, userRepository);
    }

    @Test
    void createAlbum_userExists_success() {
        UUID userId = UUID.randomUUID();
        AlbumDTO albumDTO = AlbumDTO.builder().name("Test Album").build();
        User user = new User();
        Album album = new Album();
        album.setId(UUID.randomUUID());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        UUID result = albumService.createAlbum(albumDTO, userId);

        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(albumRepository).save(any(Album.class));
    }

    @Test
    void createAlbum_userNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        AlbumDTO albumDTO = AlbumDTO.builder().name("Test Album").build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.createAlbum(albumDTO, userId));
    }

    @Test
    void getAllAlbumsByUserId_success() {
        UUID userId = UUID.randomUUID();
        Album album = new Album();
        album.setId(UUID.randomUUID());
        album.setName("Test Album");

        when(albumRepository.findAllByUser_Id(userId)).thenReturn(List.of(album));

        List<AlbumDTO> result = albumService.getAllAlbumsByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(album.getId(), result.get(0).getId());
    }

    @Test
    void getAllAlbumsByUserId_noAlbums_throwsException() {
        UUID userId = UUID.randomUUID();

        when(albumRepository.findAllByUser_Id(userId)).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, () -> albumService.getAllAlbumsByUserId(userId));
    }

    @Test
    void updateAlbum_albumExists_success() {
        UUID albumId = UUID.randomUUID();
        Album album = new Album();
        album.setId(albumId);
        album.setName("Old Name");

        AlbumDTO albumDTO = AlbumDTO.builder().id(albumId).name("New Name").build();

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        albumService.updateAlbum(albumDTO);

        verify(albumRepository).save(album);
        assertEquals("New Name", album.getName());
    }

    @Test
    void updateAlbum_albumNotFound_throwsException() {
        UUID albumId = UUID.randomUUID();
        AlbumDTO albumDTO = AlbumDTO.builder().id(albumId).name("New Name").build();

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.updateAlbum(albumDTO));
    }

    @Test
    void deleteAlbum_albumExists_success() {
        UUID albumId = UUID.randomUUID();
        Album album = new Album();
        album.setId(albumId);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        albumService.deleteAlbum(albumId);

        verify(albumRepository).delete(album);
    }

    @Test
    void deleteAlbum_albumNotFound_throwsException() {
        UUID albumId = UUID.randomUUID();

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.deleteAlbum(albumId));
    }
}
