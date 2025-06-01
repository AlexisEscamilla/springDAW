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
import com.example.rolas.DTO.ConversationSimpleDTO;
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

        // Obtener todos los mensajes de las conversaciones del usuarioaaaaaaaaa
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
        
     // Obtén solo los grupos donde el usuario participa
        List<Conversations> gruposPropios = conversaciones.stream()
            .filter(Conversations::isGroup)
            .collect(Collectors.toList());

        model.addAttribute("gruposPropios", gruposPropios);
        	
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
    public ResponseEntity<?> createChat(
            @RequestParam(required = false) String groupName,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("userId") Integer agrega) {

        // Obtener el usuario autenticado
        User creator = userRepositorio.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener el usuario agregado
        User agregado = userRepositorio.findById(agrega)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

     // Buscar si ya existe un chat individual entre estos dos usuarios
        List<ConversationParticipants> creatorParticipations = conversationParticipantsRepositorio.findByUserId(creator.getId());
        List<ConversationParticipants> agregadoParticipations = conversationParticipantsRepositorio.findByUserId(agregado.getId());
        
     // Obtener los IDs de las conversaciones individuales de cada usuario
        List<Integer> creatorChats = creatorParticipations.stream()
            .map(cp -> cp.getConversation())
            .filter(conv -> !conv.isGroup())
            .map(Conversations::getId)
            .collect(Collectors.toList());

        List<Integer> agregadoChats = agregadoParticipations.stream()
            .map(cp -> cp.getConversation())
            .filter(conv -> !conv.isGroup())
            .map(Conversations::getId)
            .collect(Collectors.toList());

        // Buscar intersección: si hay un chat individual compartido, ya existe
        creatorChats.retainAll(agregadoChats);
        if (!creatorChats.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Ya existe un chat individual entre estos usuarios.");
        }

        
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

        // Retornar DTO
        ConversationSimpleDTO dto = new ConversationSimpleDTO();
        dto.setId(conversation.getId());
        dto.setGroupName(conversation.getGroupName());
        dto.setIsGroup(conversation.isGroup());
        dto.setGroupPhoto(conversation.getGroupPhoto());
        return ResponseEntity.ok(dto);
    }
    	
    @PostMapping("/createGroup")
    @ResponseBody
    public ConversationSimpleDTO createGroup(
            @RequestParam("groupName") String groupName,
            @RequestParam(value = "groupPhoto", required = false) MultipartFile groupPhoto,
            @RequestParam("userIds") List<Integer> userIds,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User creator = userRepositorio.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String grupoFoto = groupPhoto != null ? groupPhoto.getOriginalFilename() : null;
        Conversations conversation = new Conversations();
        conversation.setGroupName(groupName);
        conversation.setCreatedBy(creator);
        conversation.setGroupPhoto(grupoFoto);
        conversation.setGroup(true);
        conversation.setCreatedAt(LocalDateTime.now());

        conversation = conversationsRepositorio.save(conversation);

        userIds.add(creator.getId()); // Agregar también al creadoraaa

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

        // Retornar DTO
        ConversationSimpleDTO dto = new ConversationSimpleDTO();
        dto.setId(conversation.getId());
        dto.setGroupName(conversation.getGroupName());
        dto.setIsGroup(conversation.isGroup());
        dto.setGroupPhoto(conversation.getGroupPhoto());
        return dto;
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
    
    
    @PostMapping("/{groupId}/agregarUsuario")
    @ResponseBody
    public ResponseEntity<?> agregarUsuarioAGrupo(
            @PathVariable Integer groupId,
            @RequestParam("userId") Integer userId,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 1. Verifica que la conversación existe y es grupoaaaa
        Conversations grupo = conversationsRepositorio.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        if (!grupo.isGroup()) {
            return ResponseEntity.badRequest().body("La conversación no es un grupo.");
        }

        // 2. Verifica que el usuario existe
        User usuario = userRepositorio.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Verifica si ya es participante
        ConversationParticipantsId cpId = new ConversationParticipantsId();
        cpId.setConversationId(groupId);
        cpId.setUserId(userId);

        boolean yaEsParticipante = conversationParticipantsRepositorio.existsById(cpId);
        if (yaEsParticipante) {
            return ResponseEntity.badRequest().body("El usuario ya es participante del grupo.");
        }

        // 4. Agrega el participante
        ConversationParticipants participante = new ConversationParticipants();
        participante.setId(cpId);
        participante.setConversation(grupo);
        participante.setUser(usuario);
        participante.setJoinedAt(LocalDateTime.now());
        conversationParticipantsRepositorio.save(participante);

        return ResponseEntity.ok("Usuario agregado al grupo correctamente.");
    }
    
    
    @PostMapping("/{chatId}/eliminar")
    @ResponseBody
    public ResponseEntity<?> eliminarChat(
            @PathVariable Integer chatId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepositorio.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Conversations chat = conversationsRepositorio.findById(chatId)
            .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        if (!chat.getCreatedBy().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo el creador puede eliminar el chat.");
        }

        // Elimina la conversación y sus participantes (por cascada)
        conversationsRepositorio.delete(chat);

        return ResponseEntity.ok("Chat eliminado correctamente.");
    }
    
    @PostMapping("/{chatId}/salir")
    @ResponseBody
    public ResponseEntity<?> salirDeGrupo(
            @PathVariable Integer chatId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepositorio.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Conversations chat = conversationsRepositorio.findById(chatId)
            .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        // Solo aplica para grupos
        if (!chat.isGroup()) {
            return ResponseEntity.badRequest().body("Solo puedes salir de grupos.");
        }

        // No permitir que el creador salga (puede eliminar el grupo, pero no salir)aaaaaaa
        if (chat.getCreatedBy().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("El creador no puede salir del grupo, solo eliminarlo.");
        }

        // Elimina al usuario de los participantes
        ConversationParticipantsId cpId = new ConversationParticipantsId();
        cpId.setConversationId(chatId);
        cpId.setUserId(user.getId());

        if (!conversationParticipantsRepositorio.existsById(cpId)) {
            return ResponseEntity.badRequest().body("No eres participante de este grupo.");
        }

        conversationParticipantsRepositorio.deleteById(cpId);

        return ResponseEntity.ok("Has salido del grupo correctamente.");
    }
}