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
    private NotificationRepository notificationRepository;
    
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsForUser(@PathVariable Integer userId) {
        List<Notification> notifications = notificationService.getNotificationsForUser(userId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        List<NotificationDTO> dtoList = notifications.stream().map(n -> {
            NotificationDTO dto = new NotificationDTO();
            dto.setId(n.getId());
            dto.setType(n.getType());
            dto.setContentId(n.getContentId());
            dto.setCreatedAt(n.getCreatedAt());
            dto.setCreatedAtFormatted(n.getCreatedAt().format(formatter));

            if (n.getSourceUser() != null) {
                dto.setSourceUser(new SourceUserDTO(
                    n.getSourceUser().getId(),
                    n.getSourceUser().getUsername()
                ));
            }

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }
}
