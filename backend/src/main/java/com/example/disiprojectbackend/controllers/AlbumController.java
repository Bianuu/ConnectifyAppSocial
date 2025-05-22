package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.AlbumDTO;
import com.example.disiprojectbackend.serviceInterfaces.AlbumServiceInterface;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/albums")
public class AlbumController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlbumController.class);
    private final AlbumServiceInterface albumServiceInterface;

    @PostMapping("/insert")
    public ResponseEntity<?> insertAlbum(@RequestBody AlbumDTO albumDTO) {
        UUID insertedId = albumServiceInterface.createAlbum(albumDTO, albumDTO.getUserId());
        LOGGER.info("Album with id " + insertedId + " was inserted in the database");
        return ResponseEntity.ok("Album inserted successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") UUID id) {
        albumServiceInterface.deleteAlbum(id);
        LOGGER.info("Album with id {} was deleted", id);
        return ResponseEntity.ok("Album with id " + id + " has been deleted successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateAlbum(@RequestBody AlbumDTO albumDTO) {
        albumServiceInterface.updateAlbum(albumDTO);
        LOGGER.info("Album with id " + albumDTO.getId() + " was updated in the database");
        return ResponseEntity.ok("Album updated successfully");
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getAllAlbumsByUserId(@PathVariable("userId") UUID userId) {
        LOGGER.info("Getting all albums for user with id {}", userId);
        List<AlbumDTO> albums = null;
        albums = albumServiceInterface.getAllAlbumsByUserId(userId);
        return ResponseEntity.ok(albums);
    }

}
