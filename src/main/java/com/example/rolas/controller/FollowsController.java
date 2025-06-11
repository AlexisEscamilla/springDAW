package com.example.rolas.controller;

import com.example.rolas.Repositorio.FollowersRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.model.Followers;
import com.example.rolas.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FollowsController {

    @Autowired
    private UserRepositorio userRepositorio;

    @Autowired
    private FollowersRepositorio followersRepositorio;

    @GetMapping("/follows/{userId}")
    public String showFollows(
            @PathVariable Integer userId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        User usuario = userRepositorio.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // los que siguen a este usuario
        List<Followers> followers = followersRepositorio.findByFollowing(usuario);
        List<User> seguidores = followers.stream()
                .map(Followers::getFollower)
                .collect(Collectors.toList());

        // los que este usuario sigue
        List<Followers> following = followersRepositorio.findByFollower(usuario);
        List<User> siguiendo = following.stream()
                .map(Followers::getFollowing)
                .collect(Collectors.toList());

        User usuarioAutenticado = userRepositorio.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));


        model.addAttribute("seguidores", seguidores);
        model.addAttribute("siguiendo", siguiendo);
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioAutenticado", usuarioAutenticado);

        return "follows";
    }
}
