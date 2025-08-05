package com.example.rolas.RestController;

import com.example.rolas.model.Notification;
import com.example.rolas.Repositorio.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificacionesRestController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/user/{userId}")
    public List<Notification> getNotificationsByUser(@PathVariable Integer userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
