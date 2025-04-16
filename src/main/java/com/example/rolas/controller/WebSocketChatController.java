package com.example.rolas.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Messages sendMessage(@Payload Map<String, Object> payload) {
        // Recuperar el ID de la conversación
        Map<String, Object> conversationMap = (Map<String, Object>) payload.get("conversation");
        Integer conversationId = Integer.parseInt(conversationMap.get("id").toString());

        // Texto del mensaje
        String messageText = (String) payload.get("mensajeText");

        // Recuperar el sender
        Map<String, String> senderMap = (Map<String, String>) payload.get("sender");
        String username = senderMap.get("username");

        // Buscar el usuario
        User sender = userRepositorio.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar la conversación
        Conversations conversation = conversationsRepositorio.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));

        // Guardar el mensaje
        Messages message = new Messages();
        message.setSender(sender);
        message.setConversation(conversation);
        message.setMensajeText(messageText);
        message.setCreatedAt(LocalDateTime.now());

        return messagesRepositorio.save(message);
    }

}