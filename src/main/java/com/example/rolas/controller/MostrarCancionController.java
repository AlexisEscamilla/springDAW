package com.example.rolas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.rolas.Repositorio.AlbumsRepositorio;
import com.example.rolas.Repositorio.SongArtistsRepositorio;
import com.example.rolas.Repositorio.SongCommentsRepositorio;
import com.example.rolas.Repositorio.SongLikesRepositorio;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.model.AlbumArtists;
import com.example.rolas.model.Albums;
import com.example.rolas.model.Artista;
import com.example.rolas.model.Post;
import com.example.rolas.model.SongComments;
import com.example.rolas.model.SongLikes;
import com.example.rolas.model.SongArtists;
import com.example.rolas.model.Songs;
import com.example.rolas.model.User;
import com.example.rolas.service.NotificationService;

@Controller
public class MostrarCancionController {
	
	@Autowired
	private SongsRepositorio songRepositorio;
	
	@Autowired
	private UserRepositorio userRepositorio;
	
	@Autowired
	private SongCommentsRepositorio songCommentsRepositorio;
	
	@Autowired
	private SongLikesRepositorio songLikesRepositorio;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private AlbumsRepositorio albumRepositorio;
	
	@Autowired
	private SongArtistsRepositorio songArtistRepositorio;
	
	@GetMapping("/mostrarCancion/{id}")
	public String mostrarCancion(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Integer id, Model model) {
	    // Obtener la canción por ID
	    Songs cancion = songRepositorio.findById(id)
	        .orElseThrow(() -> new RuntimeException("Canción no encontrada"));

	    // Obtener los comentarios relacionados con la canción
	    List<SongComments> comentarios = songCommentsRepositorio.findBySong(cancion);
	    Albums album = cancion.getAlbum();

	    User usuarioAutenticado = userRepositorio.findByUsername(userDetails.getUsername())
	            .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
	    
	    // Obtener los artistas de la canción aaaaaaaaaaaaaaaaaaaaaa
	    List<SongArtists> artistasCancion = songArtistRepositorio.findBySong(cancion);
	    
	    List<Artista> creadores = List.of();
	    creadores = artistasCancion.stream()
                .map(SongArtists::getArtista)
                .distinct()
                .toList();
	    
	    boolean esPropietario = false;
	    for(Artista personaLogueado : creadores) {
	    	if(usuarioAutenticado.getArtista() != null) {
	    		if(usuarioAutenticado.getArtista().equals(personaLogueado)) {
		    		esPropietario = true;
		    	}else {
		    		esPropietario = false;
		    	}
	    	}
	    }
	    
	    List<String> nombresArtistas = artistasCancion.stream()
	        .filter(sa -> sa.getArtista() != null && sa.getArtista().getUser() != null)
	        .map(sa -> sa.getArtista().getUser().getNombre())
	        .toList();

	    boolean userHasLiked = songLikesRepositorio.existsByUserAndSong(usuarioAutenticado, cancion);
	    
	    // Pasar la canción, los comentarios, el álbum y los artistas al modeloaaaaaaaaaaaaaaaaaa
	    model.addAttribute("nombresArtistas", nombresArtistas);
	    model.addAttribute("cancion", cancion);
	    model.addAttribute("comentarios", comentarios);
	    model.addAttribute("album", album);
	    model.addAttribute("artistas", artistasCancion);
	    model.addAttribute("esPropietario", esPropietario);
	    model.addAttribute("userHasLiked", userHasLiked);
	    return "mostrarCancion";
	}
	
	@PostMapping("/songs/{id}/comment")
	public String agregarComentario(@PathVariable Integer id,
	                                @RequestParam("comment") String commentText,
	                                @AuthenticationPrincipal UserDetails userDetails) {
	    // Obtener la canción
	    Songs cancion = songRepositorio.findById(id)
	        .orElseThrow(() -> new RuntimeException("Canción no encontrada"));

	    // Obtener el usuario autenticado
	    User user = userRepositorio.findByUsername(userDetails.getUsername())
	        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

	    // Crear el comentario
	    SongComments comentario = new SongComments();
	    comentario.setSong(cancion);
	    comentario.setUser(user);
	    comentario.setCommentText(commentText);

	    // Guardar el comentario
	    songCommentsRepositorio.save(comentario);

	    // Crear una notificación para cada artista de la canción
	    cancion.getSongArtists().forEach(artist -> {
	        notificationService.createNotification(
	            artist.getArtista().getUser(), // Usuario que recibe la notificación (artista)
	            "commentSongs",        // Tipo de notificación
	            user,             // Usuario que hizo el comentario
	            cancion.getId()   // ID de la canción
	        );
	    });

	    // Redirigir a la página de la canción
	    return "redirect:/mostrarCancion/" + id;
	}
	
}
