package com.example.disiprojectbackend.repositories;

import com.example.disiprojectbackend.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    List<Photo> findAllByAlbumId(UUID albumId);
}
