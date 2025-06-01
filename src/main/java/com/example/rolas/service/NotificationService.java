package com.example.rolas.service;

import com.example.rolas.model.Notification;
import com.example.rolas.model.User;
import com.example.rolas.Repositorio.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

}