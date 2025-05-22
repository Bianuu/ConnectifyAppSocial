package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.AlbumDTO;
import com.example.disiprojectbackend.serviceInterfaces.AlbumServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlbumControllerTest {

    @Mock
    private AlbumServiceInterface albumServiceInterface;

    @InjectMocks
    private AlbumController albumController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("insertAlbum - success")
    void insertAlbum_success() {
        AlbumDTO album = AlbumDTO.builder().userId(UUID.randomUUID()).build();
        when(albumServiceInterface.createAlbum(eq(album), eq(album.getUserId())))
                .thenReturn(UUID.randomUUID());

        var response = albumController.insertAlbum(album);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Album inserted successfully", response.getBody());
        verify(albumServiceInterface).createAlbum(eq(album), eq(album.getUserId()));
    }

    @Test
    @DisplayName("insertAlbum - null album")
    void insertAlbum_nullAlbum() {
        assertThrows(NullPointerException.class, () -> albumController.insertAlbum(null));
    }

    @Test
    @DisplayName("deleteUser - success")
    void deleteUser_success() {
        UUID albumId = UUID.randomUUID();

        var response = albumController.deleteUser(albumId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains(albumId.toString()));
        verify(albumServiceInterface).deleteAlbum(albumId);
    }

    @Test
    @DisplayName("updateAlbum - success")
    void updateAlbum_success() {
        AlbumDTO album = AlbumDTO.builder().id(UUID.randomUUID()).build();

        var response = albumController.updateAlbum(album);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Album updated successfully", response.getBody());
        verify(albumServiceInterface).updateAlbum(album);
    }

    @Test
    @DisplayName("updateAlbum - null album")
    void updateAlbum_nullAlbum() {
        assertThrows(NullPointerException.class, () -> albumController.updateAlbum(null));
    }

    @Test
    @DisplayName("getAllAlbumsByUserId - returns list")
    void getAllAlbumsByUserId_returnsList() {
        UUID userId = UUID.randomUUID();
        List<AlbumDTO> albums = List.of(AlbumDTO.builder().id(UUID.randomUUID()).build());
        when(albumServiceInterface.getAllAlbumsByUserId(userId)).thenReturn(albums);

        var response = albumController.getAllAlbumsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(albums, response.getBody());
    }

    @Test
    @DisplayName("getAllAlbumsByUserId - returns empty list")
    void getAllAlbumsByUserId_returnsEmpty() {
        UUID userId = UUID.randomUUID();
        when(albumServiceInterface.getAllAlbumsByUserId(userId)).thenReturn(Collections.emptyList());

        var response = albumController.getAllAlbumsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }
}
