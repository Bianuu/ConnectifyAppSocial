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
public class UserDTO {

    private UUID id;
    private String email;
    private String username;
    private String password;
    private String bio;
    private String role;
    private Date createdAt;
    private Date updatedAt;
    private byte[] image;
    private Date dateOfBirth;


}
