package com.example.rolas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.Repositorio.AlbumsRepositorio;

@Controller
public class BuscadorController {

    @Autowired
    private SongsRepositorio songsRepositorio;

    @Autowired
    private UserRepositorio userRepositorio;

    @Autowired
    private AlbumsRepositorio albumsRepositorio;

    @GetMapping("/buscar")
    public String buscar(@RequestParam("query") String query, Model model) {
        // Buscar canciones, artistas y álbumes
        model.addAttribute("canciones", songsRepositorio.findByTitleContainingIgnoreCase(query));
        model.addAttribute("usuarios", userRepositorio.findByNombreContainingIgnoreCase(query));
        model.addAttribute("albums", albumsRepositorio.findByTitleContainingIgnoreCase(query));

        return "resultadosBusqueda";
    }
}