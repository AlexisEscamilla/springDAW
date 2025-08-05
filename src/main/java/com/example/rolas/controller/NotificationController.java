package com.example.rolas.controller;

import com.example.rolas.DTO.NotificationDTO;
import com.example.rolas.DTO.SourceUserDTO;
import com.example.rolas.model.Notification;
import com.example.rolas.model.User;
import com.example.rolas.service.NotificationService;
import com.example.rolas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/mostrarNotificaciones")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String getNotifications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Notification> notificaciones = notificationService.getNotificationsForUser(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("notificaciones", notificaciones);

        return "mostrarNotificaciones";
    }

    @PostMapping
    public Notification createNotification(
            @RequestParam String type,
            @RequestParam Integer userId,
            @RequestParam(required = false) Integer sourceUserId,
            @RequestParam(required = false) Integer contentId) {
        User user = userService.findById(userId);
        User sourceUser = sourceUserId != null ? userService.findById(sourceUserId) : null;
        return notificationService.createNotification(user, type, sourceUser, contentId);
    }
    
    @GetMapping("/data")
    @ResponseBody
    public List<NotificationDTO> getNotificationsData(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Notification> notifications = notificationService.getNotificationsForUser(user.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        return notifications.stream().map(notification -> {
            NotificationDTO dto = new NotificationDTO();
            dto.setId(notification.getId());
            dto.setType(notification.getType());
            dto.setCreatedAt(notification.getCreatedAt());
            dto.setCreatedAtFormatted(notification.getCreatedAt().format(formatter));
            dto.setContentId(notification.getContentId());

            SourceUserDTO sourceUserDTO = new SourceUserDTO();
            if (notification.getSourceUser() != null) {
                sourceUserDTO.setId(notification.getSourceUser().getId());
                sourceUserDTO.setUsername(notification.getSourceUser().getUsername());
            }
            dto.setSourceUser(sourceUserDTO);

            return dto;
        }).toList();
    }
    
}
