package com.example.rolas.service;

import com.example.rolas.model.Notification;
import com.example.rolas.model.User;
import com.example.rolas.DTO.NotificationDTO;
import com.example.rolas.DTO.SourceUserDTO;
import com.example.rolas.Repositorio.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Notification> getNotificationsForUser(Integer userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    
    public List<NotificationDTO> getNotificationsForUsuario(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(notification -> {
            NotificationDTO dto = new NotificationDTO();
            dto.setId(notification.getId());
            dto.setType(notification.getType());
            dto.setCreatedAt(notification.getCreatedAt());
            dto.setContentId(notification.getContentId());

            if (notification.getSourceUser() != null) {
                dto.setSourceUser(new SourceUserDTO(
                    notification.getSourceUser().getId(),
                    notification.getSourceUser().getUsername()
                ));
            }

            dto.setCreatedAtFormatted(notification.getCreatedAt().toString()); // puedes formatear aquí
            return dto;
        }).collect(Collectors.toList());
    }
    

}