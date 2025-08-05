package com.example.rolas.RestController;

import com.example.rolas.model.Notification;
import com.example.rolas.model.User;
import com.example.rolas.service.NotificationService;
import com.example.rolas.service.UserService;
import com.example.rolas.DTO.NotificationDTO;
import com.example.rolas.DTO.SourceUserDTO;
import com.example.rolas.Repositorio.NotificationRepository;
import com.example.rolas.config.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
	
@RestController
@RequestMapping("/api")
public class NotificacionesRestController {

    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/notificaciones/{idUser}")
    public ResponseEntity<?> getNotificationsForUser(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        User currentUser = userService.findByUsername(username);
        
        List<NotificationDTO> dtoList = notificationService.getNotificationsForUser(currentUser.getId());
        return ResponseEntity.ok(dtoList);
    }
}
