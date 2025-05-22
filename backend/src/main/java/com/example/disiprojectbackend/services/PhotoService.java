package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.PhotoDTO;
import com.example.disiprojectbackend.DTOs.builders.PhotoBuilder;
import com.example.disiprojectbackend.entities.Album;
import com.example.disiprojectbackend.entities.Photo;
import com.example.disiprojectbackend.repositories.AlbumRepository;
import com.example.disiprojectbackend.repositories.PhotoRepository;
import com.example.disiprojectbackend.serviceInterfaces.PhotoServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Builder
@AllArgsConstructor
public class PhotoService implements PhotoServiceInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoService.class);
    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;
    private ModelMapper modelMapper;

    @Override
    public String uploadAlbumImage(UUID albumId, MultipartFile file) {
        Optional<Album> albumOptional = albumRepository.findById(albumId);
        if (albumOptional.isEmpty()) {
            LOGGER.error("Album with id {} was not found", albumId);
            return "Album not found";
        }

        Album album = albumOptional.get();

        try {
            PhotoDTO photoDTO = PhotoDTO.builder()
                    .albumId(albumId)
                    .image(file.getBytes())
                    .createdAt(new Date())
                    .build();

            Photo photo = PhotoBuilder.toEntity(photoDTO);
            photo.setAlbum(album);
            photoRepository.save(photo);
            LOGGER.info("Photo uploaded successfully for album with id {}", albumId);
            return "Photo uploaded successfully";
        } catch (IOException e) {
            LOGGER.error("Error uploading photo for album with id {}", albumId, e);
            return "Error uploading photo";
        }
    }

    @Override
    public void deletePhoto(UUID photoId) {
        Optional<Photo> photoOptional = photoRepository.findById(photoId);
        if (!photoOptional.isPresent()) {
            LOGGER.error("Photo with id {} was not found", photoId);
            throw new EntityNotFoundException("Photo not found");
        }
        Photo photo = photoOptional.get();
        photoRepository.delete(photo);
        LOGGER.info("Photo with id {} was deleted", photoId);
    }

    @Transactional
    @Override
    public List<PhotoDTO> getAllPhotosByAlbumId(UUID albumId) {
        List<Photo> photos = photoRepository.findAllByAlbumId(albumId);
        if (photos.isEmpty()) {
            LOGGER.error("Album with id {} has no photos", albumId);
            throw new EntityNotFoundException("Album has no photos");
        }
        return photos.stream().map(PhotoBuilder::toPhotoDTO).collect(Collectors.toList());
    }
}
