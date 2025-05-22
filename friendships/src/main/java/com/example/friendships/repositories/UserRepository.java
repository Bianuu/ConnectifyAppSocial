package com.example.friendships.repositories;

import com.example.friendships.entities.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends Neo4jRepository<User, UUID> {
    @Query("MATCH (u1:User {id: $id1})-[r:FRIENDS_WITH]-(u2:User {id: $id2}) DELETE r")
    void deleteFriendship(UUID id1, UUID id2);

    @Query("MATCH (u:User {id: $id}) DETACH DELETE u")
    void deleteUserAndRelationships(UUID id);

    @Query("MATCH (u:User {id: $id}) SET u.username = $username, u.image = $image RETURN u")
    User updateUser(UUID id, String username, byte[] image);

    @Query("""
            MATCH (u:User {id: $userId})-[:FRIENDS_WITH]->(:User)-[:FRIENDS_WITH]->(foaf:User)
            WHERE NOT (u)-[:FRIENDS_WITH]-(foaf) AND u.id <> foaf.id
            RETURN DISTINCT foaf
            """)
    List<User> suggestFriends(UUID userId);


}
