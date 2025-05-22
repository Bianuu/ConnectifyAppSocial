package com.example.disiprojectbackend.DTOs.builders;

import com.example.disiprojectbackend.DTOs.AlbumDTO;
import com.example.disiprojectbackend.entities.Album;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Builder
@NoArgsConstructor
public class AlbumBuilder {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static AlbumDTO toAlbumDTO(Album album) {
        return modelMapper.map(album, AlbumDTO.class);
    }

    public static Album toEntity(AlbumDTO albumDTO) {
        return modelMapper.map(albumDTO, Album.class);
    }
}
