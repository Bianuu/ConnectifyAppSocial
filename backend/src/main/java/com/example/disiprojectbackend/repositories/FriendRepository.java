package com.example.disiprojectbackend.repositories;

import com.example.disiprojectbackend.entities.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FriendRepository extends JpaRepository<Friend, UUID> {
}
