package com.example.rolas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.rolas.Repositorio.FollowersRepositorio;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.model.SongComments;
import com.example.rolas.model.Songs;
import com.example.rolas.model.User;
import com.example.rolas.model.Artista;
import com.example.rolas.model.Followers;


@Controller
public class MostrarPerfilController {

    @Autowired
    private UserRepositorio userRepositorio;	
    
    @Autowired
    private SongsRepositorio songsRepositorio;	
    
    @Autowired
    private FollowersRepositorio followersrepositorio;

    @GetMapping("/mostrarPerfil/{id}")
    public String mostrarPerfil(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails,Model model) {
        // Obtener el usuario por ID
    	// Obtener el usuario del perfil
        User usuario = userRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener el usuario autenticado
        User usuarioAutenticado = userRepositorio.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        // Verificar si el usuario autenticado ya sigue al usuario del perfil
        boolean isFollowing = followersrepositorio.existsByFollowerAndFollowing(usuarioAutenticado, usuario);

        Artista artista = usuario.getArtista();
     // Obtener los comentarios relacionados con la canción
	    List<Followers> seguidores = followersrepositorio.findByFollower(usuario);
	    List<Songs> canciones = songsRepositorio.findByArtistaId(artista.getId());
	    List<Followers> siguiendo = followersrepositorio.findByFollowing(usuario);

        // Pasar el usuario al modelo
        model.addAttribute("usuario", usuario);
        model.addAttribute("seguidores", seguidores);
        model.addAttribute("siguiendo", siguiendo);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("canciones", canciones);

        // Retornar la vista mostrarPerfil.html
        return "mostrarPerfil";
    }
}
