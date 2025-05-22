package com.example.friendships.controllers;

import com.example.friendships.dtos.FriendSuggestionDTO;
import com.example.friendships.services.SuggestionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/suggestions")
@AllArgsConstructor
public class SuggestionController {

    private final SuggestionService suggestionService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<FriendSuggestionDTO>> getSuggestions(@PathVariable UUID userId) {
        List<FriendSuggestionDTO> suggestions = suggestionService.suggestFriends(userId);
        return ResponseEntity.ok(suggestions);
    }
}
