package com.example.disiprojectbackend.DTOs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {


    private UUID id;
    private UUID senderMessage;
    private UUID receiverMessage;
    private Date sentAt;

    private String content;
}
