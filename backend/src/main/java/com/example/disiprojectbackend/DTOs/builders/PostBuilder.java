package com.example.disiprojectbackend.DTOs.builders;

import com.example.disiprojectbackend.DTOs.PostDTO;
import com.example.disiprojectbackend.entities.Post;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Builder
@NoArgsConstructor
public class PostBuilder {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static PostDTO toPostDTO(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .userId(post.getUser().getId())
                .image(post.getImage())
                .postAuthor(post.getUser().getUsername2()) // ✅ setează autorul
                .build();
    }


    public static Post toEntity(PostDTO postDTO) {
        return modelMapper.map(postDTO, Post.class);
    }
}
