package com.example.rolas.controller;

import com.example.rolas.model.User;
import com.example.rolas.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.AlbumsRepositorio;
import com.example.rolas.Repositorio.PlaylistsRepositorio;
import com.example.rolas.Repositorio.SongCommentsRepositorio;
import com.example.rolas.model.Songs;
import com.example.rolas.model.Albums;
import com.example.rolas.model.Playlists;
import com.example.rolas.model.SongComments;

@Controller
public class WebController {
	
	@Autowired
	private UserRepositorio userRepositorio;
	
	
	@Autowired
	private AlbumsRepositorio albumRepositorio;
	
	@Autowired
	private PlaylistsRepositorio playlistsRepositorio;
	
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

    @GetMapping("/mostrarAlbum/{id}")
    public String mostrarAlbum(@PathVariable Integer id, Model model) {
        Albums album = albumRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Álbum no encontrado"));
        model.addAttribute("album", album);
        return "mostrarAlbum";
    }
    
    @GetMapping("/mostrarPlaylist/{id}")
    public String mostrarPlaylist(@PathVariable Integer id, Model model) {
        Playlists playlist = playlistsRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Playlist no encontrada"));
        model.addAttribute("playlist", playlist);
        return "mostrarPlaylist";
    }
    
}
