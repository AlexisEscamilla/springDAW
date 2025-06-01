package com.example.rolas.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.model.Songs;
import com.example.rolas.model.User;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private SongsRepositorio songsRepositorio;
    
    @Autowired
    private UserRepositorio userRepositorio;

    @GetMapping("/buscarUsuarios")
    public List<Map<String, Object>> buscarUsuarios(@RequestParam String query) {
        List<User> usuarios;
        if (query == null || query.isBlank()) {
            usuarios = Collections.emptyList(); // No devolver nada si no hay query
        } else {
            // Busca usuarios por nombre o username que contengan la query
            usuarios = userRepositorio.findByNombreContainingIgnoreCaseOrUsernameContainingIgnoreCase(query, query);
        }
        
        // Mapea solo los campos necesarios
        return usuarios.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("nombre", user.getNombre());
            map.put("username", user.getUsername());
            // Puedes agregar más campos si los necesitas en el frontend
            return map;
        }).collect(Collectors.toList());
    }
    
    @GetMapping("/canciones")
    public List<Map<String, Object>> buscarCanciones(@RequestParam(required = false) String query) {
        List<Songs> canciones;
        if (query == null || query.isBlank()) {
            canciones = songsRepositorio.findAll();
        } else {
            canciones = songsRepositorio.findByTitleContainingIgnoreCase(query);
        }
        // Devuelve solo los datos necesarios para el modal
        return canciones.stream().map(song -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", song.getId());
            map.put("title", song.getTitle());
            map.put("coverUrl", song.getCoverUrl());
            map.put("artists", song.getSongArtists().stream()
                .map(sa -> sa.getArtista().getUser().getNombre())
                .collect(Collectors.joining(", ")));
            return map;
        }).collect(Collectors.toList());
    }
}