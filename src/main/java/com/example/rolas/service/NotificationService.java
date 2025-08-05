package com.example.rolas.service;

import com.example.rolas.model.Notification;
import com.example.rolas.model.User;
import com.example.rolas.DTO.NotificationDTO;
import com.example.rolas.DTO.SourceUserDTO;
import com.example.rolas.Repositorio.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(User user, String type, User sourceUser, Integer contentId) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setSourceUser(sourceUser);
        notification.setContentId(contentId);
        return notificationRepository.save(notification);
    }
    
    public List<NotificationDTO> getNotificationsForUser(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return notifications.stream().map(n -> {
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
    }

    

}