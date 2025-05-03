package com.example.rolas.controller;

import com.example.rolas.model.User;
import com.example.rolas.service.PostService;
import com.example.rolas.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.example.rolas.Repositorio.AlbumArtistsRepositorio;
import com.example.rolas.Repositorio.AlbumsRepositorio;
import com.example.rolas.Repositorio.ArtistaRepositorio;
import com.example.rolas.Repositorio.PlaylistsRepositorio;
import com.example.rolas.Repositorio.SongCommentsRepositorio;
import com.example.rolas.model.Songs;
import com.example.rolas.model.Albums;
import com.example.rolas.model.Artista;
import com.example.rolas.model.Playlists;
import com.example.rolas.model.SongComments;
import com.example.rolas.model.AlbumArtists;
import com.example.rolas.model.Post;

@Controller
public class WebController {
	
	@Autowired
	private UserRepositorio userRepositorio;
	
	@Autowired
	private SongsRepositorio songsRepositorio;
	
	@Autowired
	private AlbumsRepositorio albumRepositorio;

	@Autowired
	private AlbumArtistsRepositorio albumArtistsRepositorio;
	
	@Autowired
	private PlaylistsRepositorio playlistsRepositorio;
	
	@Autowired
	private PostService postService;
	
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
    
    @GetMapping("/explorar")
    public String explorar() {
        return "explorar";
    }
   

    @GetMapping("/mostrarAlbum/{id}")
    public String mostrarAlbum(@PathVariable Integer id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Albums album = albumRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Álbum no encontrado"));
        
        List<Songs> cancionesDelAlbum = songsRepositorio.findByAlbumId(album.getId());
        
        // Obtener las relaciones de AlbumArtists
        List<AlbumArtists> relaciones = albumArtistsRepositorio.findByAlbumId(album.getId());
        
        // Extraer los artistas de las relaciones
        List<User> artistasAlbum = relaciones.stream()
            .map(AlbumArtists::getUsuario) // Suponiendo que AlbumArtists tiene un método getArtista()
            .collect(Collectors.toList());
        
        model.addAttribute("album", album);
        model.addAttribute("songs", cancionesDelAlbum);
        model.addAttribute("artistas", artistasAlbum); // Enviar los artistas al modelo
        
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
