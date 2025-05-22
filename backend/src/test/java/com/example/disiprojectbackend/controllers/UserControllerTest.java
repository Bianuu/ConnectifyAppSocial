package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.UserDTO;
import com.example.disiprojectbackend.serviceInterfaces.UserServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserServiceInterface userServiceInterface;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("uploadImage - success")
    void uploadImage_success() {
        UUID userId = UUID.randomUUID();
        MultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
        when(userServiceInterface.uploadUserImage(eq(userId), any())).thenReturn("Image uploaded successfully");

        var response = userController.uploadImage(userId, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Image uploaded successfully", response.getBody());
    }

    @Test
    @DisplayName("uploadImage - failure")
    void uploadImage_failure() {
        UUID userId = UUID.randomUUID();
        MultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
        when(userServiceInterface.uploadUserImage(eq(userId), any())).thenReturn("Failure");

        var response = userController.uploadImage(userId, file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Failure", response.getBody());
    }

    @Test
    @DisplayName("getAllUsers - should clear passwords")
    void getAllUsers_shouldClearPasswords() {
        UserDTO user = UserDTO.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("irrelevant")
                .build();
        when(userServiceInterface.getAllUsers()).thenReturn(List.of(user));

        var response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UserDTO> users = (List<UserDTO>) response.getBody();
        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertEquals("", users.get(0).getPassword());
    }

    @Test
    @DisplayName("insertUser - email already exists")
    void insertUser_emailExists() {
        UserDTO user = UserDTO.builder().email("exists@example.com").build();
        when(userServiceInterface.checkEmailExists("exists@example.com")).thenReturn(true);

        var response = userController.insertUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("insertUser - validation fails")
    void insertUser_validationFails() {
        UserDTO user = UserDTO.builder()
                .email("")
                .password("")
                .username("")
                .bio("")
                .createdAt(new Date())
                .updatedAt(new Date())
                .dateOfBirth(new Date())
                .build();

        when(userServiceInterface.checkEmailExists(any())).thenReturn(false);

        var response = userController.insertUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("insertUser - success")
    void insertUser_success() {
        UserDTO user = UserDTO.builder()
                .email("new@example.com")
                .password("password7")
                .username("newuser")
                .bio("bio")
                .createdAt(new Date())
                .updatedAt(new Date())
                .dateOfBirth(new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime())
                .build();
        when(userServiceInterface.checkEmailExists("new@example.com")).thenReturn(false);
        when(userServiceInterface.createUser(any())).thenReturn(UUID.randomUUID());

        var response = userController.insertUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("deleteUser - success")
    void deleteUser_success() {
        UUID userId = UUID.randomUUID();

        var response = userController.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userServiceInterface).deleteUser(userId);
    }

    @Test
    @DisplayName("updateUser - email exists for another user")
    void updateUser_emailExistsForOtherUser() {
        UserDTO user = UserDTO.builder()
                .id(UUID.randomUUID())
                .email("exists@example.com")
                .build();
        when(userServiceInterface.checkEmailExistsForOtherUser(user.getEmail(), user.getId())).thenReturn(true);

        var response = userController.updateUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("updateUser - validation fails")
    void updateUser_validationFails() {
        UserDTO user = UserDTO.builder()
                .id(UUID.randomUUID())
                .email("")
                .password("")
                .username("")
                .bio("")
                .dateOfBirth(new Date())
                .build();
        when(userServiceInterface.checkEmailExistsForOtherUser(user.getEmail(), user.getId())).thenReturn(false);

        var response = userController.updateUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("updateUser - success")
    void updateUser_success() {
        UserDTO user = UserDTO.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("password7")
                .username("testuser")
                .bio("bio")
                .dateOfBirth(new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime())
                .build();
        when(userServiceInterface.checkEmailExistsForOtherUser(user.getEmail(), user.getId())).thenReturn(false);

        var response = userController.updateUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userServiceInterface).updateUser(user);
    }

    @Test
    @DisplayName("login - success")
    void login_success() {
        UserDTO user = UserDTO.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .build();
        when(userServiceInterface.getUserByEmailAndPassword("test@example.com", "password")).thenReturn(user);

        var response = userController.login("test@example.com", "password");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }
}
