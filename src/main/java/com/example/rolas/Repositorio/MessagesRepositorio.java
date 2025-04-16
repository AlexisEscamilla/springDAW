package com.example.rolas.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.rolas.model.Messages;

import java.util.List;

public interface MessagesRepositorio extends JpaRepository<Messages, Integer> {
    List<Messages> findByConversationIdOrderByCreatedAtAsc(Integer conversationId);
    List<Messages> findByConversationId(Integer conversationId);
    List<Messages> findByConversationIdInOrderByCreatedAtAsc(List<Integer> conversationIds);
}