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

import com.example.rolas.Repositorio.SongCommentsRepositorio;
import com.example.rolas.Repositorio.SongLikesRepositorio;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.model.SongComments;
import com.example.rolas.model.SongLikes;
import com.example.rolas.model.Songs;
import com.example.rolas.model.User;

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
	
	@GetMapping("/mostrarCancion/{id}")
	public String mostrarCancion(@PathVariable Integer id, Model model) {
	    // Obtener la canción por ID
	    Songs cancion = songRepositorio.findById(id)
	        .orElseThrow(() -> new RuntimeException("Canción no encontrada"));

	    // Obtener los comentarios relacionados con la canción
	    List<SongComments> comentarios = songCommentsRepositorio.findBySong(cancion);

	    // Pasar la canción y los comentarios al modelo
	    model.addAttribute("cancion", cancion);
	    model.addAttribute("comentarios", comentarios);

	    // Retornar la vista mostrarCancion.html
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

	    // Redirigir a la página de la canción
	    return "redirect:/mostrarCancion/" + id;
	}
	
}
