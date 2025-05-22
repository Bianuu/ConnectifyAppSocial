package com.example.disiprojectbackend.DTOs.builders;

import com.example.disiprojectbackend.DTOs.FriendDTO;
import com.example.disiprojectbackend.entities.Friend;
import com.example.disiprojectbackend.entities.User;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;


@Builder
@NoArgsConstructor
public class FriendBuilder {


    private static final ModelMapper modelMapper = new ModelMapper();

    public static FriendDTO toFriendDTO(Friend friend) {
        return FriendDTO.builder()
                .id(friend.getId())
                .user1Id(friend.getUser1().getId())
                .user2Id(friend.getUser2().getId())
                .createdAt(friend.getCreatedAt())
                .build();
    }

    public static Friend toEntity(FriendDTO dto, User user1, User user2) {
        return Friend.builder()
                .id(dto.getId())
                .user1(user1)
                .user2(user2)
                .createdAt(dto.getCreatedAt())
                .build();
    }
}
