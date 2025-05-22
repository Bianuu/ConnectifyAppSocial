package com.example.disiprojectbackend.DTOs.builders;

import com.example.disiprojectbackend.DTOs.PhotoDTO;
import com.example.disiprojectbackend.entities.Photo;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Builder
@NoArgsConstructor
public class PhotoBuilder {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static PhotoDTO toPhotoDTO(Photo photo) {
        return modelMapper.map(photo, PhotoDTO.class);
    }

    public static Photo toEntity(PhotoDTO photoDTO) {
        return modelMapper.map(photoDTO, Photo.class);
    }
}
