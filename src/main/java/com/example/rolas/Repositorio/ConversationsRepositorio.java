package com.example.rolas.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.rolas.model.Conversations;

public interface ConversationsRepositorio extends JpaRepository<Conversations, Integer> {
	Optional<Conversations> findById(Integer id);
}