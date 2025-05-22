package com.example.disiprojectbackend.DTOs.builders;

import com.example.disiprojectbackend.DTOs.FriendRequestDTO;
import com.example.disiprojectbackend.entities.FriendRequest;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Builder
@NoArgsConstructor
public class FriendRequestBuilder {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static FriendRequestDTO toFriendReqestDTO(FriendRequest friendRequest) {
        return modelMapper.map(friendRequest, FriendRequestDTO.class);
    }

    public static FriendRequest toEntity(FriendRequestDTO friendRequestDTO) {
        return modelMapper.map(friendRequestDTO, FriendRequest.class);
    }
}
