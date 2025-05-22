package com.example.disiprojectbackend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotificationDTO {
    private UUID id;
    private UUID senderId;
    private UUID recipientId;
    private String content;
}