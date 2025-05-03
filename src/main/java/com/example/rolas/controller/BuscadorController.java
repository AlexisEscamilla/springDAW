package com.example.rolas.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.DTO.AlbumDTO;
import com.example.rolas.DTO.CancionDTO;
import com.example.rolas.DTO.UserDTO;
import com.example.rolas.Repositorio.AlbumsRepositorio;

@Controller
public class BuscadorController {

    @Autowired
    private SongsRepositorio songsRepositorio;

    @Autowired
    private UserRepositorio userRepositorio;

    @Autowired
    private AlbumsRepositorio albumsRepositorio;

    
    
    @GetMapping("/api/buscar")
    @ResponseBody
    public Map<String, Object> buscarJson(@RequestParam("query") String query) {
        Map<String, Object> resultados = new HashMap<>();

        List<CancionDTO> canciones = songsRepositorio.findByTitleContainingIgnoreCase(query)
            .stream()
            .map(CancionDTO::new)
            .collect(Collectors.toList());

        List<UserDTO> usuarios = userRepositorio.findByNombreContainingIgnoreCase(query)
            .stream()
            .map(UserDTO::new)
            .collect(Collectors.toList());

        List<AlbumDTO> albums = albumsRepositorio.findByTitleContainingIgnoreCase(query)
            .stream()
            .map(AlbumDTO::new)
            .collect(Collectors.toList());

        resultados.put("canciones", canciones);
        resultados.put("usuarios", usuarios);
        resultados.put("albums", albums);
        return resultados;
    }


}