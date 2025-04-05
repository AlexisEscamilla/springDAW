package com.example.rolas.controller;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.rolas.Repositorio.AlbumsRepositorio;
import com.example.rolas.Repositorio.AlbumArtistsRepositorio;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.model.Albums;
import com.example.rolas.model.Artista;
import com.example.rolas.model.Songs;
import com.example.rolas.model.User;
import com.example.rolas.service.UserService;
import java.util.List;

@Controller
public class ColeccionesController {
	
	@Autowired
    private UserRepositorio userRepositorio;

    @Autowired
    private SongsRepositorio songsRepositorio;

    @Autowired
    private AlbumsRepositorio albumsRepositorio;
    
    @Autowired
    private AlbumArtistsRepositorio albumsartistsRepositorio;
	
	@GetMapping("/colecciones")
    public String mostrarColecciones(@AuthenticationPrincipal UserDetails userDetails,
                                      @RequestParam(value = "tipo", defaultValue = "canciones") String tipo,
                                      Model model) {
        // Obtener el usuario autenticado
        Optional<User> usuarioOpt = userRepositorio.findByUsername(userDetails.getUsername());

        if (usuarioOpt.isPresent()) {
            User usuario = usuarioOpt.get();
            model.addAttribute("usuario", usuario);

            Artista artista = usuario.getArtista();
            if (artista != null) {
                if (tipo.equals("canciones")) {
                    // Obtener las canciones del artista
                    List<Songs> canciones = songsRepositorio.findByArtistaId(artista.getId());
                    model.addAttribute("canciones", canciones);
                } else if (tipo.equals("albums")) {
                    // Obtener los álbumes del artista
                    List<Albums> albums = albumsRepositorio.findByAlbumArtists_Artista(artista);
                    model.addAttribute("albums", albums);
                }
            } else {
                model.addAttribute("canciones", List.of());
                model.addAttribute("albums", List.of());
            }
        } else {
            return "redirect:/feed";
        }

        model.addAttribute("tipo", tipo); // Indicar el tipo seleccionado
        return "colecciones";
    }
	
}
