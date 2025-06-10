package com.example.rolas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.rolas.Repositorio.FollowersRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.model.Followers;
import com.example.rolas.model.User;
import com.example.rolas.service.NotificationService;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/users")
public class FollowersController {

    @Autowired
    private FollowersRepositorio followersRepositorio;

    @Autowired
    private UserRepositorio userRepositorio;

    @Autowired
    private NotificationService notificationService;
    
    @PostMapping("/{id}/follow")
    @ResponseBody
    public String followUser(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        // se obtiene el usuario autenticado
        User follower = userRepositorio.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // obtiene el usuario a seguir
        User following = userRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // verifica si ya lo sigue
        if (followersRepositorio.existsByFollowerAndFollowing(follower, following)) {
            return "already_following";
        }

        // crea la relacion de seguimiento
        Followers follow = new Followers();
        follow.setFollower(follower);
        follow.setFollowing(following);
        followersRepositorio.save(follow);

        // incrementa los contadores
        following.setSeguidores(following.getSeguidores() + 1);
        follower.setSiguiendo(follower.getSiguiendo() + 1);

        userRepositorio.save(following);
        userRepositorio.save(follower);

        // crea una notificacion para el usuario seguido
        notificationService.createNotification(
            following,       
            "new_follower",  
            follower,       
            null             
        );

        return "followed";
    }

    @PostMapping("/{id}/unfollow")
    @ResponseBody
    @Transactional
    public String unfollowUser(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        User follower = userRepositorio.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        User following = userRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        System.out.println("Follower: " + follower.getUsername());
        System.out.println("Following: " + following.getUsername());

        if (!followersRepositorio.existsByFollowerAndFollowing(follower, following)) {
            System.out.println("No existe la relación de seguimiento.");
            return "not_following";
        }

        // elimina la relacion de seguimiento
        followersRepositorio.deleteByFollowerAndFollowing(follower, following);

        // decrementa los contadores
        following.setSeguidores(Math.max(0, following.getSeguidores() - 1));
        follower.setSiguiendo(Math.max(0, follower.getSiguiendo() - 1));

        userRepositorio.save(following);
        userRepositorio.save(follower);

        System.out.println("Relación eliminada correctamente.");
        return "unfollowed";
    }
}