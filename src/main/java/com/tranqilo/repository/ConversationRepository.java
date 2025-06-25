package com.tranqilo.repository;

import com.tranqilo.model.Conversation;
import com.tranqilo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // Find all conversations a specific user is a part of
    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.id = :userId")
    List<Conversation> findByParticipantId(@Param("userId") Long userId);

    // Find a conversation that has exactly two specific participants
    @Query("SELECT c FROM Conversation c WHERE :user1 MEMBER OF c.participants AND :user2 MEMBER OF c.participants AND SIZE(c.participants) = 2")
    Optional<Conversation> findByParticipants(@Param("user1") User user1, @Param("user2") User user2);
}