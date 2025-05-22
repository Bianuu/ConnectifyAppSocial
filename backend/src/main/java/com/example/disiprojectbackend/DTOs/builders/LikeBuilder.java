package com.example.disiprojectbackend.DTOs.builders;

import com.example.disiprojectbackend.DTOs.LikeDTO;
import com.example.disiprojectbackend.entities.Like;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Builder
@NoArgsConstructor
public class LikeBuilder {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static LikeDTO toLikeDTO(Like like) {
        return modelMapper.map(like, LikeDTO.class);
    }

    public static Like toEntity(LikeDTO likeDTO) {
        return modelMapper.map(likeDTO, Like.class);
    }
}
