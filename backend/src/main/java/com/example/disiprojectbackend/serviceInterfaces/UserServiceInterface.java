package com.example.disiprojectbackend.serviceInterfaces;

import com.example.disiprojectbackend.DTOs.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserServiceInterface {

    UUID createUser(UserDTO userDTO);

    UserDTO getUserById(UUID userId);

    UserDTO getUserByNameAndPassword(String name, String password);


    List<UserDTO> getAllUsers();

    void updateUser(UserDTO userDTO);

    void deleteUser(UUID id);

    boolean checkEmailExists(String email);

    String uploadUserImage(UUID userId, MultipartFile file);

    boolean checkEmailExistsForOtherUser(String email, UUID userId);

    UserDTO getUserByEmailAndPassword(String email, String password);

    void editPasswordById(UUID id, String password);

    UserDTO getUserByEmail(String email);

}
