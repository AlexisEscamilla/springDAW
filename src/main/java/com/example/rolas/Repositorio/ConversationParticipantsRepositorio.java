package com.example.rolas.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.rolas.model.ConversationParticipants;
import com.example.rolas.model.ConversationParticipantsId;

public interface ConversationParticipantsRepositorio extends JpaRepository<ConversationParticipants, ConversationParticipantsId> {
    List<ConversationParticipants> findByUserId(Integer userId);
    List<ConversationParticipants> findByConversationIdIn(List<Integer> conversationIds);
    boolean existsById(ConversationParticipantsId id);
}