package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.FriendRequestDTO;
import com.example.disiprojectbackend.DTOs.builders.FriendRequestBuilder;
import com.example.disiprojectbackend.entities.FriendRequest;
import com.example.disiprojectbackend.repositories.FriendRequestRepository;
import com.example.disiprojectbackend.serviceInterfaces.FriendRequestInterface;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Builder
@AllArgsConstructor
public class FriendRequestService implements FriendRequestInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(FriendRequestService.class);
    private final FriendRequestRepository friendRequestRepository;
    private ModelMapper modelMapper;

    @Override
    public UUID createFriendRequest(FriendRequestDTO friendRequestDTO) {
        FriendRequest friendRequest = FriendRequestBuilder.toEntity(friendRequestDTO);
        friendRequest = friendRequestRepository.save(friendRequest);
        LOGGER.info("User with id {} inserted", friendRequest.getId());
        return friendRequest.getId();
    }


    @Override
    public FriendRequestDTO getFriendRequestById(UUID friendRequestId) {
        Optional<FriendRequest> friendRequestOptional = friendRequestRepository.findById(friendRequestId);
        if (!friendRequestOptional.isPresent()) {

            LOGGER.error("Friend Request with id {} was not found", friendRequestId);
        }
        return FriendRequestBuilder.toFriendReqestDTO(friendRequestOptional.get());
    }


    @Override
    public List<FriendRequestDTO> getAllFriendRequest() {
        List<FriendRequest> friendRequests = friendRequestRepository.findAll();
        return friendRequests.stream().map(FriendRequestBuilder::toFriendReqestDTO).collect(Collectors.toList());
    }

    @Override
    public void updateFriendRequest(FriendRequestDTO friendRequestDTO) {

        Optional<FriendRequest> friendRequestOptional = friendRequestRepository.findById(friendRequestDTO.getId());
        if (!friendRequestOptional.isPresent()) {

            LOGGER.info("Client with id {} was not found", friendRequestDTO.getId());
        }
        FriendRequest friendRequest = friendRequestOptional.get();
        friendRequest.setStatus(friendRequestDTO.getStatus());
        friendRequestRepository.save(friendRequest);
        LOGGER.info("Friend request with id {} was updated in db ", friendRequestDTO.getId());

    }


    @Override
    public void deleteFriendRequest(UUID id) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findById(id);
        if (!friendRequest.isPresent()) {
            LOGGER.error("Friend request with id {} was not found in db", id);
        }
        friendRequestRepository.deleteById(id);
        LOGGER.info("Friend request with id {} was deleted from db", id);

    }

    @Override
    public List<FriendRequestDTO> getAllFriendRequestsByReceiverId(UUID senderId) {
        List<FriendRequest> friendRequests = friendRequestRepository.findAllByReceiverId(senderId);
        return friendRequests.stream().map(FriendRequestBuilder::toFriendReqestDTO).collect(Collectors.toList());
    }


}
