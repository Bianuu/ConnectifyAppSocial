package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.UserDTO;
import com.example.disiprojectbackend.configs.SuggestionMessageProducer;
import com.example.disiprojectbackend.entities.User;
import com.example.disiprojectbackend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private SuggestionMessageProducer suggestionMessageProducer;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        suggestionMessageProducer = mock(SuggestionMessageProducer.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder, new ModelMapper(), suggestionMessageProducer);
    }

    @Test
    void uploadUserImage_userExists_success() throws IOException {
        UUID userId = UUID.randomUUID();
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
        String result = userService.uploadUserImage(userId, file);

        assertEquals("Image uploaded successfully", result);
        verify(userRepository).save(user);
    }

    @Test
    void uploadUserImage_userNotFound_returnsError() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        String result = userService.uploadUserImage(userId, null);

        assertEquals("User not found", result);
    }

    @Test
    void checkEmailExistsForOtherUser_existsAndDifferentUser_true() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertTrue(userService.checkEmailExistsForOtherUser("test@example.com", userId));
    }

    @Test
    void checkEmailExistsForOtherUser_notExists_false() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertFalse(userService.checkEmailExistsForOtherUser("test@example.com", UUID.randomUUID()));
    }

    @Test
    void createUser_success() {
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UUID result = userService.createUser(UserDTO.builder().build());

        assertNotNull(result);
    }

    @Test
    void getUserById_found_success() {
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(user.getId());

        assertNotNull(result);
    }

    @Test
    void getUserById_notFound_throwsException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getAllUsers_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of(new User()));

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
    }

    @Test
    void updateUser_found_success() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenAnswer(i -> i.getArgument(0));

        UserDTO userDTO = UserDTO.builder()
                .id(userId)
                .email("test@example.com")
                .username("test@example.com")
                .password("pass")
                .bio("bio")
                .build();

        userService.updateUser(userDTO);

        verify(userRepository).save(user);
        verify(suggestionMessageProducer).sendSuggestionMessage(user, user, "update");

        assertEquals("test@example.com", user.getEmail(), "Email should be updated");
        assertEquals("test@example.com", user.getUsername(), "Username should be updated");
        assertEquals("pass", user.getPassword(), "Password should be updated");
        assertEquals("bio", user.getBio(), "Bio should be updated");
    }


    @Test
    void deleteUser_found_success() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
        verify(suggestionMessageProducer).sendSuggestionMessage(user, user, "user_delete");
    }

    @Test
    void deleteUser_notFound_throwsException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.deleteUser(userId));
    }

    @Test
    void checkEmailExists_true() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertTrue(userService.checkEmailExists("test@example.com"));
    }

    @Test
    void checkEmailExists_false() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        assertFalse(userService.checkEmailExists("test@example.com"));
    }

    @Test
    void getUserByEmailAndPassword_foundAndMatches_success() {
        User user = new User();
        user.setPassword("pass");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserByEmailAndPassword("test@example.com", "pass");

        assertNotNull(result);
    }

    @Test
    void getUserByEmailAndPassword_foundButWrongPassword_throwsException() {
        User user = new User();
        user.setPassword("pass");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> userService.getUserByEmailAndPassword("test@example.com", "wrong"));
    }

    @Test
    void getUserByEmailAndPassword_notFound_throwsException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.getUserByEmailAndPassword("test@example.com", "pass"));
    }
}
