package com.example.rolas.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.rolas.model.ConversationParticipants;

public interface ConversationParticipantsRepositorio extends JpaRepository<ConversationParticipants, Integer> {
	List<ConversationParticipants> findByUserId(Integer userId);
	List<ConversationParticipants> findByConversationIdIn(List<Integer> conversationIds);

}