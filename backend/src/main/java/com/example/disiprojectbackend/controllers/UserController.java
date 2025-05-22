package com.example.disiprojectbackend.controllers;

import com.example.disiprojectbackend.DTOs.EmailDTO;
import com.example.disiprojectbackend.DTOs.UserDTO;
import com.example.disiprojectbackend.configs.MessageProducer;
import com.example.disiprojectbackend.serviceInterfaces.UserServiceInterface;
import com.example.disiprojectbackend.validators.UserUpdateValidator;
import com.example.disiprojectbackend.validators.UserValidator;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserServiceInterface userServiceInterface;
    private MessageProducer messageProducer;

    @PostMapping("/{id}/uploadImage")
    public ResponseEntity<String> uploadImage(@PathVariable UUID id, @RequestParam("image") MultipartFile file) {
        String responseMessage = userServiceInterface.uploadUserImage(id, file);
        if (responseMessage.equals("Image uploaded successfully")) {
            return ResponseEntity.ok(responseMessage);
        } else {
            return ResponseEntity.badRequest().body(responseMessage);
        }
    }


    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        LOGGER.info("Getting all users");
        List<UserDTO> users = null;

        users = userServiceInterface.getAllUsers();
        users.forEach(client -> client.setPassword(""));

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") UUID userId) {
        LOGGER.info("Getting user by id {}", userId);
        UserDTO userDTO = userServiceInterface.getUserById(userId);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertUser(@RequestBody UserDTO userDTO) {

        if (userServiceInterface.checkEmailExists(userDTO.getEmail())) {
            Map<String, String> response = Map.of(
                    "error", "Email already exists!"
            );
            return ResponseEntity.badRequest().body(response);
        }


        List<String> validationErrors = UserValidator.validateWholeDataForUser(
                userDTO.getEmail(),
                userDTO.getPassword(),
                userDTO.getUsername(),
                userDTO.getBio(),
                userDTO.getCreatedAt(),
                userDTO.getUpdatedAt(),
                userDTO.getDateOfBirth()
        );

        if (!validationErrors.isEmpty()) {
            Map<String, Object> response = Map.of(
                    "error", "Validation failed",
                    "details", validationErrors
            );
            LOGGER.info("Datele introduse nu sunt corecte: " + validationErrors);
            return ResponseEntity.badRequest().body(response);
        }


        UUID insertedId = userServiceInterface.createUser(userDTO);
        LOGGER.info("User with id " + insertedId + " was inserted in the database");

        Map<String, Object> response = Map.of(
                "message", "User inserted successfully",
                "userId", insertedId.toString()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") UUID id) {
        userServiceInterface.deleteUser(id);
        LOGGER.info("User with id {} was deleted", id);
        return ResponseEntity.ok("User with id " + id + " has been deleted successfully");
    }

    @PostMapping("/reset-password/{email}")
    public ResponseEntity<Void> resetPassword(@PathVariable("email") String email) {

        UserDTO userDTO = userServiceInterface.getUserByEmail(email);
        EmailDTO emailDTO = new EmailDTO(email, userDTO.getId());

        messageProducer.sendMessage(emailDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{id}/{password}")
    public ResponseEntity<?> updateUser(@PathVariable("id") UUID id, @PathVariable("password") String password) {


        userServiceInterface.editPasswordById(id, password);
        LOGGER.info("User with id {} was updated", id);

        Map<String, String> response = Map.of(
                "message", "User with id " + id + " has been updated successfully"
        );

        return ResponseEntity.ok(response);
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO) {

        if (userServiceInterface.checkEmailExistsForOtherUser(userDTO.getEmail(), userDTO.getId())) {
            Map<String, String> response = Map.of(
                    "error", "Email already exists!"
            );
            return ResponseEntity.badRequest().body(response);
        }

        List<String> validationErrors = UserUpdateValidator.validateWholeDataForUser(
                userDTO.getEmail(),
                userDTO.getPassword(),
                userDTO.getUsername(),
                userDTO.getBio(),
                userDTO.getDateOfBirth()
        );

        if (!validationErrors.isEmpty()) {
            Map<String, Object> response = Map.of(
                    "error", "Validation failed",
                    "details", validationErrors
            );
            LOGGER.info("Datele introduse nu sunt corecte: " + validationErrors);
            return ResponseEntity.badRequest().body(response);
        }
        userServiceInterface.updateUser(userDTO);
        LOGGER.info("User with id {} was updated", userDTO.getId());

        Map<String, String> response = Map.of(
                "message", "User with id " + userDTO.getId() + " has been updated successfully"
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/login/{email}/{password}")
    public ResponseEntity<UserDTO> login(@PathVariable("email") String email,
                                         @PathVariable("password") String password) {
        UserDTO userDTO = userServiceInterface.getUserByEmailAndPassword(email, password);
        LOGGER.info("Cautare user");
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/updatee/{id}/{password}")
    public ResponseEntity<?> updateUser(@PathVariable("id") String id, @PathVariable("password") String password) {
        try {
            UUID userId = UUID.fromString(id); // conversia manuală
            userServiceInterface.editPasswordById(userId, password);
            LOGGER.info("User with id {} was updated", userId);

            Map<String, String> response = Map.of(
                    "message", "User with id " + userId + " has been updated successfully"
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // cazul în care id-ul nu este un UUID valid
            LOGGER.error("Invalid UUID string: {}", id);
            return ResponseEntity.badRequest().body("Invalid UUID format: " + id);
        }
    }


}
