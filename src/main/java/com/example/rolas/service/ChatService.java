package com.example.rolas.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.rolas.Repositorio.ConversationParticipantsRepositorio;
import com.example.rolas.model.User;
import com.example.rolas.model.Conversations;
import com.example.rolas.model.ConversationParticipants;

@Service
public class ChatService {

    @Autowired
    private ConversationParticipantsRepositorio conversationParticipantsRepositorio;

    public Map<Integer, User> getOneToOnePartners(User currentUser, List<Conversations> conversations) {
        Map<Integer, User> oneToOnePartners = new HashMap<>();

        List<Integer> conversationIds = conversations.stream()
            .map(Conversations::getId)
            .collect(Collectors.toList());

        List<ConversationParticipants> participants = conversationParticipantsRepositorio.findByConversationIdIn(conversationIds);

        for (Conversations conv : conversations) {
            if (!conv.isGroup()) {
                List<User> participantsInConversation = participants.stream()
                    .filter(p -> p.getConversation().getId().equals(conv.getId()))
                    .map(ConversationParticipants::getUser)
                    .collect(Collectors.toList());

                User partner = participantsInConversation.stream()
                    .filter(u -> !u.getId().equals(currentUser.getId()))
                    .findFirst()
                    .orElse(null);

                if (partner != null) {
                    oneToOnePartners.put(conv.getId(), partner);
                }
            }
        }

        return oneToOnePartners;
    }
}
