package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.AlbumDTO;
import com.example.disiprojectbackend.DTOs.builders.AlbumBuilder;
import com.example.disiprojectbackend.entities.Album;
import com.example.disiprojectbackend.entities.User;
import com.example.disiprojectbackend.repositories.AlbumRepository;
import com.example.disiprojectbackend.repositories.UserRepository;
import com.example.disiprojectbackend.serviceInterfaces.AlbumServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Builder
@AllArgsConstructor
public class AlbumService implements AlbumServiceInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlbumService.class);
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private ModelMapper modelMapper;

    @Override
    public UUID createAlbum(AlbumDTO albumDTO, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        Album newAlbum = AlbumBuilder.toEntity(albumDTO);
        newAlbum.setUser(user);
        newAlbum.setCreatedAt(new Date());
        newAlbum.setNumberOfPhotos(0);
        newAlbum = albumRepository.save(newAlbum);
        LOGGER.info("Album with id {} was inserted in db", newAlbum.getId());
        return newAlbum.getId();
    }

    @Transactional(readOnly = true)
    @Override
    public List<AlbumDTO> getAllAlbumsByUserId(UUID userId) {
        List<Album> albums = albumRepository.findAllByUser_Id(userId);
        if (albums.isEmpty()) {
            LOGGER.error("User with id {} has no posts", userId);
            throw new EntityNotFoundException("User has no albums");
        }
        return albums.stream().map(AlbumBuilder::toAlbumDTO).collect(Collectors.toList());
    }


    @Override
    public void updateAlbum(AlbumDTO albumDTO) {
        Optional<Album> albumOptional = albumRepository.findById(albumDTO.getId());
        if (!albumOptional.isPresent()) {
            LOGGER.error("Album with id {} was not found", albumDTO.getId());
            throw new EntityNotFoundException("Album not found");
        }
        Album album = albumOptional.get();
        album.setName(albumDTO.getName());
        albumRepository.save(album);
        LOGGER.info("Album with id {} was updated", album.getId());
    }

    @Override
    public void deleteAlbum(UUID id) {
        Optional<Album> albumOptional = albumRepository.findById(id);
        if (!albumOptional.isPresent()) {
            LOGGER.error("Album with id {} was not found", id);
            throw new EntityNotFoundException("Album not found");
        }
        Album album = albumOptional.get();
        albumRepository.delete(album);
        LOGGER.info("Album with id {} was deleted", id);
    }
}
