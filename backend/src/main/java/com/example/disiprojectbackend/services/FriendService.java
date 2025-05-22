package com.example.disiprojectbackend.services;

import com.example.disiprojectbackend.DTOs.FriendDTO;
import com.example.disiprojectbackend.DTOs.builders.FriendBuilder;
import com.example.disiprojectbackend.configs.SuggestionMessageProducer;
import com.example.disiprojectbackend.entities.Friend;
import com.example.disiprojectbackend.entities.FriendRequest;
import com.example.disiprojectbackend.entities.User;
import com.example.disiprojectbackend.repositories.FriendRepository;
import com.example.disiprojectbackend.repositories.FriendRequestRepository;
import com.example.disiprojectbackend.repositories.UserRepository;
import com.example.disiprojectbackend.serviceInterfaces.FriendInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Builder
@AllArgsConstructor
public class FriendService implements FriendInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(FriendService.class);
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final SuggestionMessageProducer suggestionMessageProducer;
    private ModelMapper modelMapper;

    @Override
    public UUID createFriend(FriendDTO friendDTO) {
        User user1 = userRepository.findById(friendDTO.getUser1Id())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id:" + friendDTO.getUser1Id()));
        User user2 = userRepository.findById(friendDTO.getUser2Id())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id:" + friendDTO.getUser2Id()));

        Friend friend = FriendBuilder.toEntity(friendDTO, user1, user2);
        friend.setCreatedAt(new Date());
        friend = friendRepository.save(friend);
        suggestionMessageProducer.sendSuggestionMessage(user1, user2, "insert");
        Optional<FriendRequest> friendRequestOptional = friendRequestRepository
                .findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
                        user1.getId(), user2.getId(),
                        user2.getId(), user1.getId());
        if (friendRequestOptional.isPresent()) {
            friendRequestRepository.delete(friendRequestOptional.get());
            LOGGER.info("Deleted accepted friend request between {} and {}", user1.getId(), user2.getId());
        }
        LOGGER.info("Friend  with id {} was inserted in db", friend.getId());
        return friend.getId();
    }


    @Override
    public FriendDTO getFriendById(UUID friendId) {
        Optional<Friend> friendOptional = friendRepository.findById(friendId);
        if (!friendOptional.isPresent()) {

            LOGGER.error("Friend  with id {} was not found", friendId);
        }
        return FriendBuilder.toFriendDTO(friendOptional.get());
    }

    @Override
    public List<FriendDTO> getAllFriends() {
        List<Friend> friends = friendRepository.findAll();
        return friends.stream().map(FriendBuilder::toFriendDTO).collect(Collectors.toList());

    }

    @Override
    public void deleteFriend(UUID id) {
        Optional<Friend> friend = friendRepository.findById(id);
        Friend copyForRabbit = friend.get();
        if (!friend.isPresent()) {
            LOGGER.error("Friend  with id {} was not found in db", id);
        }
        friendRepository.deleteById(id);
        LOGGER.info("Friend with id {} was deleted from db", id);
        suggestionMessageProducer.sendSuggestionMessage(copyForRabbit.getUser1(), copyForRabbit.getUser2(), "delete");
        LOGGER.info("Friend for deletion sent to RABBIT");
    }
}
