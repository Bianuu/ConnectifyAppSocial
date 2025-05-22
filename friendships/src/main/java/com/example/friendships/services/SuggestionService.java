package com.example.friendships.services;

import com.example.friendships.dtos.FriendSuggestionDTO;
import com.example.friendships.entities.User;
import com.example.friendships.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Builder
@AllArgsConstructor
public class SuggestionService {
    private UserRepository graphUserRepository;

    public void handleFriendSuggestion(FriendSuggestionDTO user1DTO, FriendSuggestionDTO user2DTO) {
        User user1 = graphUserRepository.findById(user1DTO.getId()).orElse(new User(user1DTO.getId(), user1DTO.getUsername(), user1DTO.getImage(), new HashSet<>()));
        User user2 = graphUserRepository.findById(user2DTO.getId()).orElse(new User(user2DTO.getId(), user2DTO.getUsername(), user2DTO.getImage(), new HashSet<>()));

        user1.getFriends().add(user2);
        user2.getFriends().add(user1);

        graphUserRepository.save(user1);
        graphUserRepository.save(user2);
    }

    public void handleFriendDeletion(FriendSuggestionDTO user1DTO, FriendSuggestionDTO user2DTO) {
        graphUserRepository.deleteFriendship(user1DTO.getId(), user2DTO.getId());
    }

    public void handleUserDeletion(UUID userId) {
        graphUserRepository.deleteUserAndRelationships(userId);
    }

    public void handleUserUpdate(FriendSuggestionDTO updatedUserDTO) {
        User user = graphUserRepository.findById(updatedUserDTO.getId())
                .orElseThrow(() -> new RuntimeException("User not found in graph"));

        user.setUsername(updatedUserDTO.getUsername());
        user.setImage(updatedUserDTO.getImage());

        graphUserRepository.save(user);
    }

    public List<FriendSuggestionDTO> suggestFriends(UUID userId) {
        List<User> suggested = graphUserRepository.suggestFriends(userId);
        return suggested.stream()
                .map(u -> new FriendSuggestionDTO(u.getId(), u.getUsername(), u.getImage()))
                .collect(Collectors.toList());
    }


}
