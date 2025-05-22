package com.example.disiprojectbackend.repositories;

import com.example.disiprojectbackend.entities.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, UUID> {
    List<Album> findAllByUser_Id(UUID userId);


}
