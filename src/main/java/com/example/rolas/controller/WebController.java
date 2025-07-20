package com.example.rolas.controller;

import com.example.rolas.model.User;
import com.example.rolas.service.UserService;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.config.JwtUtil;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.AlbumArtistsRepositorio;
import com.example.rolas.Repositorio.AlbumsRepositorio;
import com.example.rolas.Repositorio.PlaylistsRepositorio;
import com.example.rolas.model.Songs;
import com.example.rolas.model.Albums;
import com.example.rolas.model.PlaylistSongs;
import com.example.rolas.model.Playlists;
import com.example.rolas.model.AlbumArtists;

@Controller
public class WebController {
	
	@Autowired
	private UserRepositorio userRepositorio;
	
	@Autowired
	private SongsRepositorio songsRepositorio;
	
	@Autowired
	private AlbumsRepositorio albumRepositorio;
	
	@Autowired
	private UserService userService;

	@Autowired
	private AlbumArtistsRepositorio albumArtistsRepositorio;
	
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
    
    @GetMapping("/explorar")
    public String explorar() {
        return "explorar";
    }
   

    @GetMapping("/mostrarAlbum/{id}")
    public String mostrarAlbum(@PathVariable Integer id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Albums album = albumRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Álbum no encontrado"));
        
        List<Songs> cancionesDelAlbum = songsRepositorio.findByAlbumId(album.getId());
        
        // obtiene las relaciones de AlbumArtists
        List<AlbumArtists> relaciones = albumArtistsRepositorio.findByAlbumId(album.getId());
        
        // extrae los artistas de las relaciones
        List<User> artistasAlbum = relaciones.stream()
            .map(AlbumArtists::getUsuario)
            .collect(Collectors.toList());
        
        model.addAttribute("album", album);
        model.addAttribute("songs", cancionesDelAlbum);
        model.addAttribute("artistas", artistasAlbum); 
        
        return "mostrarAlbum";
    }
    
    @GetMapping("/mostrarPlaylist/{id}")
    public String mostrarPlaylist(@PathVariable Integer id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
    	Playlists playlist = playlistsRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist no encontrada"));
    	User usuarioLogueado = userRepositorio.findByUsername(userDetails.getUsername()).orElse(null);
    	boolean esCreador = playlist.getUser().getId().equals(usuarioLogueado.getId());
    	
    	// ordena la lista de PlaylistSongs por el campo posicion (convertido a int)
        List<PlaylistSongs> cancionesOrdenadas = playlist.getPlaylistSongs().stream()
            .sorted(Comparator.comparingInt(ps -> {
                try {
                    return Integer.parseInt(ps.getPosicion());
                } catch (Exception e) {
                    return 0; 
                }
            }))
            .toList();
        model.addAttribute("cancionesOrdenadas", cancionesOrdenadas);
    	model.addAttribute("esCreador", esCreador);
        model.addAttribute("playlist", playlist);
        
        return "mostrarPlaylist";
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userService.authenticateUser(loginRequest);

        if (userOptional.isPresent()) {
            String token = JwtUtil.generateToken(loginRequest.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", loginRequest.getUsername());
            
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }

    
}
