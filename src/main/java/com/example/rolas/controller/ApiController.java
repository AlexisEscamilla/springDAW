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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.rolas.DTO.AlbumDTO;
import com.example.rolas.DTO.CancionDTO;
import com.example.rolas.DTO.PlaylistsDTO;
import com.example.rolas.DTO.UserDTO;
import com.example.rolas.Repositorio.AlbumsRepositorio;
import com.example.rolas.Repositorio.PlaylistsRepositorio;
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
    
    @Autowired
    private AlbumsRepositorio albumsRepositorio;

    @Autowired
    private PlaylistsRepositorio playlistsRepositorio;

    @GetMapping("/buscarUsuarios")
    public List<Map<String, Object>> buscarUsuarios(@RequestParam String query) {
        List<User> usuarios;
        if (query == null || query.isBlank()) {
            usuarios = Collections.emptyList();
        } else {
            // busca usuarios por nombre o username que contengan la query
            usuarios = userRepositorio.findByNombreContainingIgnoreCaseOrUsernameContainingIgnoreCase(query, query);
        }
        
        // mapea solo los campos necesarios
        return usuarios.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("nombre", user.getNombre());
            map.put("username", user.getUsername());
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
        // devuelve solo los datos necesarios
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
    
    @GetMapping("/buscar")
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

        List<PlaylistsDTO> playlists =  playlistsRepositorio.findByTitleContainingIgnoreCase(query)
        		.stream()
                .map(PlaylistsDTO::new)
                .collect(Collectors.toList());
        
        resultados.put("canciones", canciones);
        resultados.put("playlists", playlists);
        resultados.put("usuarios", usuarios);
        resultados.put("albums", albums);
        return resultados;
    }
}