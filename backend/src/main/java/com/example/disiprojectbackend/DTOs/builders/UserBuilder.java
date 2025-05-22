package com.example.disiprojectbackend.DTOs.builders;

import com.example.disiprojectbackend.DTOs.UserDTO;
import com.example.disiprojectbackend.entities.User;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Builder
@NoArgsConstructor
public class UserBuilder {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername2()) // <- aici e schimbarea!
                .password(user.getPassword())
                .bio(user.getBio())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .image(user.getImage())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }


    public static User toEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}