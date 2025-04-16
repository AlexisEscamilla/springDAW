package com.example.rolas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.rolas.Repositorio.ConversationsRepositorio;
import com.example.rolas.Repositorio.FollowersRepositorio;
import com.example.rolas.Repositorio.MessagesRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.DTO.ChatDetailsDTO;
import com.example.rolas.DTO.ConversationDTO;
import com.example.rolas.DTO.MessageDTO;
import com.example.rolas.Repositorio.ConversationParticipantsRepositorio;
import com.example.rolas.model.Conversations;
import com.example.rolas.model.Followers;
import com.example.rolas.model.Messages;
import com.example.rolas.model.User;
import com.example.rolas.model.ConversationParticipants;
import com.example.rolas.model.ConversationParticipantsId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chats")
public class ChatsController {

    @Autowired
    private ConversationsRepositorio conversationsRepositorio;

    @Autowired
    private MessagesRepositorio messagesRepositorio;

    @Autowired
    private UserRepositorio userRepositorio;
    
    @Autowired
    private FollowersRepositorio followersRepositorio;
    
    
    @Autowired
    private ConversationParticipantsRepositorio conversationParticipantsRepositorio;
    
    @Autowired
    private ChatService chatService;
    
    @GetMapping
    public String getChats(@RequestParam(required = false) Integer selectedChatId,
    	    Model model,
    	    @AuthenticationPrincipal UserDetails userDetails) {
        // 1. Obtener el usuario autenticado
        User user = userRepositorio.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        
        List<ConversationParticipants> participacionesUsuario = conversationParticipantsRepositorio.findByUserId(user.getId());

        List<Conversations> conversaciones = participacionesUsuario.stream()
            .map(ConversationParticipants::getConversation)
            .distinct()
            .collect(Collectors.toList());
        
        List<Integer> conversationIds = conversaciones.stream()
            .map(Conversations::getId)
            .collect(Collectors.toList());

        List<ConversationParticipants> participaciones = conversationParticipantsRepositorio.findByConversationIdIn(conversationIds);

        // Obtener todos los mensajes de las conversaciones del usuario
        List<Messages> mensajes = messagesRepositorio.findByConversationIdInOrderByCreatedAtAsc(conversationIds);
        
        Map<Integer, User> oneToOnePartners = chatService.getOneToOnePartners(user, conversaciones);

     // Convertir los mensajes a DTOs
        List<MessageDTO> mensajesDTO = mensajes.stream().map(msg -> {
            MessageDTO dto = new MessageDTO();
            dto.setTexto(msg.getMensajeText());
            dto.setSenderName(msg.getSender().getNombre());
            dto.setSenderPhoto(msg.getSender().getProfilePicture());
            dto.setMine(msg.getSender().getId().equals(user.getId()));
            dto.setConversationId(msg.getConversation().getId());
            System.out.println(dto.getConversationId());
            dto.setCreatedAt(msg.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
        
        
        List<Followers> followers = followersRepositorio.findByFollower(user);

        List<User> usuarios = followers.stream()
            .map(Followers::getFollowing)
            .collect(Collectors.toList());
        
        // 8. Agregar al modelo
        model.addAttribute("user", user);
        model.addAttribute("conversations", conversaciones);
        model.addAttribute("participaciones", participaciones); // si lo usas en la vista
        model.addAttribute("followers", usuarios);
        model.addAttribute("oneToOnePartners", oneToOnePartners);
        model.addAttribute("messages", mensajesDTO);
        
        return "chats";
    }


    @GetMapping("/chats/{chatId}/messages")
    @ResponseBody
    public ResponseEntity<List<MessageDTO>> getMessages(
        @PathVariable Integer chatId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = userRepositorio.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Messages> mensajes = messagesRepositorio.findByConversationIdOrderByCreatedAtAsc(chatId);

        List<MessageDTO> dtos = mensajes.stream().map(msg -> {
            User sender = msg.getSender();
            MessageDTO dto = new MessageDTO();
            dto.setTexto(msg.getMensajeText());
            dto.setSenderName(sender.getNombre());
            dto.setSenderPhoto(sender.getProfilePicture());
            dto.setMine(sender.getId().equals(currentUser.getId()));
            dto.setCreatedAt(msg.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    
    @GetMapping("/chats")
    @ResponseBody
    public List<ConversationDTO> getConversations() {
        // Recuperar las conversaciones de la base de datos
        List<Conversations> conversations = conversationsRepositorio.findAll();

        // Convertir las entidades a DTOs
        return conversations.stream()
            .map(conv -> new ConversationDTO(conv.getId(), conv.getGroupName()))
            .collect(Collectors.toList());
    }
    
    @PostMapping("/create")
    @ResponseBody
    public Conversations createChat(@RequestParam(required = false) String groupName, 
                                    @AuthenticationPrincipal UserDetails userDetails, 
                                    @RequestParam("userId") Integer agrega) {
        

        // Obtener el usuario autenticado
        User creator = userRepositorio.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener el usuario agregado
        User agregado = userRepositorio.findById(agrega)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear la conversación
        Conversations conversation = new Conversations();
        conversation.setGroupName(groupName);
        conversation.setCreatedBy(creator);
        conversation.setGroup(false); // No es un grupo
        conversation.setCreatedAt(LocalDateTime.now());
        conversation = conversationsRepositorio.save(conversation);

        // Agregar el creador como participante
        ConversationParticipants participant = new ConversationParticipants();
        ConversationParticipantsId participantId = new ConversationParticipantsId();
        participantId.setConversationId(conversation.getId());
        participantId.setUserId(creator.getId());
        participant.setId(participantId);
        participant.setConversation(conversation);
        participant.setUser(creator);
        participant.setJoinedAt(LocalDateTime.now());
        conversationParticipantsRepositorio.save(participant);

        // Agregar el usuario seleccionado como participante
        ConversationParticipants participant2 = new ConversationParticipants();
        ConversationParticipantsId participantId2 = new ConversationParticipantsId();
        participantId2.setConversationId(conversation.getId());
        participantId2.setUserId(agregado.getId());
        participant2.setId(participantId2);
        participant2.setConversation(conversation);
        participant2.setUser(agregado);
        participant2.setJoinedAt(LocalDateTime.now());
        conversationParticipantsRepositorio.save(participant2);

        return conversation;
    }
    
    @PostMapping("/createGroup")
    public ResponseEntity<?> createGroup(
            @RequestParam("groupName") String groupName,
            @RequestParam(value = "groupPhoto", required = false) MultipartFile groupPhoto,
            @RequestParam("userIds") List<Integer> userIds,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (groupName == null || groupName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre del grupo es obligatorio.");
        }

        if (userIds == null || userIds.size() < 2) {
            return ResponseEntity.badRequest().body("Debes seleccionar al menos 2 usuarios.");
        }

        User creator = userRepositorio.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        String grupoFoto = groupPhoto.getOriginalFilename();
        Conversations conversation = new Conversations();
        conversation.setGroupName(groupName);
        conversation.setCreatedBy(creator);
        conversation.setGroupPhoto(grupoFoto);
        conversation.setGroup(true);
        conversation.setCreatedAt(LocalDateTime.now());


        conversation = conversationsRepositorio.save(conversation);

        userIds.add(creator.getId()); // Agregar también al creador

        for (Integer id : userIds) {
            User user = userRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            ConversationParticipants participant = new ConversationParticipants();
            ConversationParticipantsId participantId = new ConversationParticipantsId();
            participantId.setConversationId(conversation.getId());
            participantId.setUserId(user.getId());
            participant.setId(participantId);
            participant.setConversation(conversation);
            participant.setUser(user);
            participant.setJoinedAt(LocalDateTime.now());

            conversationParticipantsRepositorio.save(participant);
        }

        return ResponseEntity.ok(conversation);
    }

    
    @GetMapping("/chats/details/{chatId}")
    @ResponseBody
    public ResponseEntity<ChatDetailsDTO> getChatDetails(@PathVariable Integer chatId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepositorio.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Conversations chat = conversationsRepositorio.findById(chatId)
            .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        ChatDetailsDTO chatDetails = new ChatDetailsDTO();
        chatDetails.setId(chat.getId());
        chatDetails.setGroup(chat.isGroup());
        chatDetails.setGroupName(chat.isGroup() ? chat.getGroupName() : null);
        chatDetails.setGroupPhoto(chat.isGroup() ? chat.getGroupPhoto() : null);
        
        System.out.println(chatDetails.getGroupPhoto());
        System.out.println(chatDetails.getGroupName());
        if (!chat.isGroup()) {
            Map<Integer, User> oneToOnePartners = chatService.getOneToOnePartners(user, List.of(chat));
            User partner = oneToOnePartners.get(chatId);

            if (partner != null) {
                chatDetails.setPartnerName(partner.getNombre());
                chatDetails.setPartnerPhoto(partner.getProfilePicture());
            }
        }

        return ResponseEntity.ok(chatDetails);
    }
}