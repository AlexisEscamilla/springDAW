package com.example.rolas.RestController;

import com.example.rolas.model.Notification;
import com.example.rolas.service.NotificationService;
import com.example.rolas.DTO.NotificationDTO;
import com.example.rolas.DTO.SourceUserDTO;
import com.example.rolas.Repositorio.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
	
@RestController
@RequestMapping("/api/notifications")
public class NotificacionesRestController {

    
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsForUser(@PathVariable Integer userId) {
        List<NotificationDTO> dtoList = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(dtoList);
    }
}
