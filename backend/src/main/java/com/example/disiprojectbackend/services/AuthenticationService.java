package com.example.disiprojectbackend.services;


import com.example.disiprojectbackend.DTOs.LoginUserDTO;
import com.example.disiprojectbackend.DTOs.UserDTO;
import com.example.disiprojectbackend.entities.User;
import com.example.disiprojectbackend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setImage(userDTO.getImage());
        user.setBio(userDTO.getBio());
        user.setCreatedAt(userDTO.getCreatedAt());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setUpdatedAt(userDTO.getUpdatedAt());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());
        System.out.println("Parola înainte de criptare: " + userDTO.getPassword());


        return userRepository.save(user);
    }

    @Transactional
    public User authenticate(LoginUserDTO input) {
        System.out.println(input.getEmail());
        System.out.println(input.getPassword());
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + input.getEmail()));

        if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("The username or password is incorrect");
        }

        return user;
    }
}