package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.UserDTO;
import com.example.disiprojectbackend.DTOs.builders.UserBuilder;
import com.example.disiprojectbackend.configs.SuggestionMessageProducer;
import com.example.disiprojectbackend.entities.User;
import com.example.disiprojectbackend.repositories.UserRepository;
import com.example.disiprojectbackend.serviceInterfaces.UserServiceInterface;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Builder
@AllArgsConstructor
public class UserService implements UserServiceInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SuggestionMessageProducer suggestionMessageProducer;
    private ModelMapper modelMapper;

    @Override
    public String uploadUserImage(UUID userId, MultipartFile file) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found", userId);
            return "User not found";
        }

        User user = userOptional.get();
        try {
            user.setImage(file.getBytes());
            userRepository.save(user);
            LOGGER.info("Image uploaded successfully for user with id {}", userId);
            return "Image uploaded successfully";
        } catch (IOException e) {
            LOGGER.error("Error uploading image for user with id {}", userId, e);
            return "Error uploading image";
        }
    }

    @Transactional
    @Override
    public boolean checkEmailExistsForOtherUser(String email, UUID userId) {
        Optional<User> userWithEmail = userRepository.findByEmail(email);
        return userWithEmail.isPresent() && !userWithEmail.get().getId().equals(userId);
    }

    @Override
    public UUID createUser(UserDTO userDTO) {
        User user = UserBuilder.toEntity(userDTO);
        user = userRepository.save(user);
        LOGGER.info("User with id {} inserted", user.getId());
        return user.getId();
    }


    @Transactional
    @Override
    public UserDTO getUserById(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {

            LOGGER.error("User with id {} was not found", userId);
        }
        return UserBuilder.toUserDTO(userOptional.get());

    }

    @Override
    public UserDTO getUserByNameAndPassword(String name, String password) {
        return null;
    }


    @Transactional(readOnly = true)
    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserBuilder::toUserDTO).collect(Collectors.toList());

    }

    @Transactional
    @Override
    public void updateUser(UserDTO userDTO) {
        Optional<User> userOptional = userRepository.findById(userDTO.getId());
        if (!userOptional.isPresent()) {

            LOGGER.info("Client with id {} was not found", userDTO.getId());
        }
        User user = userOptional.get();

        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setUpdatedAt(new Date());
        user.setBio(userDTO.getBio());
        user.setImage(userDTO.getImage());
        userRepository.save(user);
        suggestionMessageProducer.sendSuggestionMessage(user, user, "update");
        LOGGER.info("Client with id {} was updated in db ", userDTO.getId());

    }

    @Transactional
    @Override
    public void editPasswordById(UUID id, String password) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {

            LOGGER.error("User with id {} was not found", id);
        }

        User user = userOptional.get();

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        suggestionMessageProducer.sendSuggestionMessage(user, user, "update");
        LOGGER.info("Client with id {} was updated in db ", id);
    }

    @Override
    public void deleteUser(UUID id) {
        Optional<User> user = userRepository.findById(id);
        User copyForRabbit = user.get();
        if (!user.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
        }
        userRepository.deleteById(id);
        LOGGER.info("Client with id {} was deleted from db", id);
        suggestionMessageProducer.sendSuggestionMessage(copyForRabbit, copyForRabbit, "user_delete");
    }


    @Override
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDTO getUserByEmailAndPassword(String email, String password) {

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            LOGGER.info("User with email {} and password provided was not found", email);
        }
        User user = userOptional.get();
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password for user: " + email);
        }
        return UserBuilder.toUserDTO(user);
    }

    @Transactional
    @Override
    public UserDTO getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (!userOptional.isPresent()) {
            LOGGER.info("User with email {} was not found", email);
            throw new NoSuchElementException("User with email " + email + " not found");
        }

        return UserBuilder.toUserDTO(userOptional.get());
    }


}