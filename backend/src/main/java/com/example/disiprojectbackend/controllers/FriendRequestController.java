package com.example.disiprojectbackend.controllers;


import com.example.disiprojectbackend.DTOs.FriendRequestDTO;
import com.example.disiprojectbackend.serviceInterfaces.FriendRequestInterface;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/friendRequests")
public class FriendRequestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FriendRequestController.class);
    private final FriendRequestInterface friendRequestInterface;

    @GetMapping("/all")
    public ResponseEntity<?> getFriendRequests() {
        LOGGER.info("Getting all friend requests");
        List<FriendRequestDTO> friendRequestDTOS = null;

        friendRequestDTOS = friendRequestInterface.getAllFriendRequest();


        return ResponseEntity.ok(friendRequestDTOS);
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertFriendRequest(@RequestBody FriendRequestDTO friendRequestDTO) {


        UUID insertedId = friendRequestInterface.createFriendRequest(friendRequestDTO);
        LOGGER.info("Friend request with id " + insertedId + " was inserted in the database");

        Map<String, Object> response = Map.of(
                "message", "Friend request inserted successfully",
                "userId", insertedId.toString()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateFriendRequest(@RequestBody FriendRequestDTO friendRequestDTO) {


        friendRequestInterface.updateFriendRequest(friendRequestDTO);

        LOGGER.info("Friend request with id {} was updated", friendRequestDTO.getId());

        Map<String, String> response = Map.of(
                "message", "User with id " + friendRequestDTO.getId() + " has been updated successfully"
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFriendRequest(@PathVariable("id") UUID id) {
        friendRequestInterface.deleteFriendRequest(id);
        LOGGER.info("Friend request with id {} was deleted", id);
        return ResponseEntity.ok("Friend request with id " + id + " has been deleted successfully");
    }

    @Transactional(readOnly = true)
    @GetMapping("/allForUser/{senderId}")
    public ResponseEntity<?> getAllFriendRequestsBySenderId(@PathVariable("senderId") UUID senderId) {
        LOGGER.info("Getting all friend requests for sender with id {}", senderId);
        List<FriendRequestDTO> friendRequestDTOS = null;
        friendRequestDTOS = friendRequestInterface.getAllFriendRequestsByReceiverId(senderId);
        return ResponseEntity.ok(friendRequestDTOS);
    }


}
