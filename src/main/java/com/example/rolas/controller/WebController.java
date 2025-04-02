package com.example.rolas.controller;

import com.example.rolas.model.User;
import com.example.rolas.service.UserService;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import com.example.rolas.Repositorio.UserRepositorio;

@Controller
public class WebController {
	
	@Autowired
	private UserRepositorio userRepositorio;
	@Autowired
    private UserService userService;
	
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    
    @GetMapping("/feed")
    public String mostrarFeed(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> usuarioOpt = userRepositorio.findByUsername(userDetails.getUsername());

        if (usuarioOpt.isPresent()) {
            model.addAttribute("usuario", usuarioOpt.get());
        } else {
            return "redirect:/login";
        }

        return "feed";
    }


    
    @GetMapping("/chats")
    public String chats() {
        return "chats";
    }
    
  
    
}
