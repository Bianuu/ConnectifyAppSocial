package com.example.disiprojectbackend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionSuggestMessageDTO {
    private FriendSuggestionDTO friendSuggestionDTO1;
    private FriendSuggestionDTO friendSuggestionDTO2;
    private String actionMessage;
}
