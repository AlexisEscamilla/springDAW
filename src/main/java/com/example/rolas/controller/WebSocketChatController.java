package com.example.rolas.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import com.example.rolas.Repositorio.ConversationsRepositorio;
import com.example.rolas.Repositorio.MessagesRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.model.Messages;
import com.example.rolas.model.User;
import com.example.rolas.model.Conversations;

@Controller
public class WebSocketChatController {

    @Autowired
    private MessagesRepositorio messagesRepositorio;

    @Autowired
    private ConversationsRepositorio conversationsRepositorio;
    
    @Autowired
    private UserRepositorio userRepositorio;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Agrega estoaaaaaaaaaaaaaaaa
    
    @MessageMapping("/chat")
    public void sendMessage(@Payload Map<String, Object> payload) {
        // ...existing code to build and save the message...
        Map<String, Object> conversationMap = (Map<String, Object>) payload.get("conversation");
        Integer conversationId = Integer.parseInt(conversationMap.get("id").toString());
        String messageText = (String) payload.get("mensajeText");
        Map<String, String> senderMap = (Map<String, String>) payload.get("sender");
        String username = senderMap.get("username");

        User sender = userRepositorio.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Conversations conversation = conversationsRepositorio.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));

        Messages message = new Messages();
        message.setSender(sender);
        message.setConversation(conversation);
        message.setMensajeText(messageText);
        message.setCreatedAt(LocalDateTime.now());

        Messages savedMessage = messagesRepositorio.save(message);

        // Envía el mensaje solo a los suscritos a este chat
        messagingTemplate.convertAndSend("/topic/messages/" + conversationId, savedMessage);
    }

}