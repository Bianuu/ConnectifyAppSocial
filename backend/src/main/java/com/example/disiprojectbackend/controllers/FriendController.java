package com.example.disiprojectbackend.controllers;


import com.example.disiprojectbackend.DTOs.FriendDTO;
import com.example.disiprojectbackend.serviceInterfaces.FriendInterface;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/friends")
public class FriendController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FriendController.class);
    private final FriendInterface friendInterface;

    @GetMapping("/all")
    public ResponseEntity<?> getFriends() {
        LOGGER.info("Getting all friends");
        List<FriendDTO> friendDTOs = null;

        friendDTOs = friendInterface.getAllFriends();


        return ResponseEntity.ok(friendDTOs);
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertFriend(@RequestBody FriendDTO friendDTO) {

        UUID insertedId = friendInterface.createFriend(friendDTO);
        LOGGER.info("Friend  with id " + insertedId + " was inserted in the database");

        Map<String, Object> response = Map.of(
                "message", "Friend  inserted successfully",
                "userId", insertedId.toString()
        );
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFriend(@PathVariable("id") UUID id) {
        friendInterface.deleteFriend(id);
        LOGGER.info("Friend  with id {} was deleted", id);
        return ResponseEntity.ok("Friend  with id " + id + " has been deleted successfully");
    }


}
