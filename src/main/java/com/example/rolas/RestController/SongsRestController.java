package com.example.rolas.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.example.rolas.DTO.SongDTO;
import com.example.rolas.DTO.SongMapper;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.config.JwtUtil;
import com.example.rolas.model.Songs;
import com.example.rolas.model.User;
import com.example.rolas.service.UserService;

@RestController
@RequestMapping("/api")
public class SongsRestController {

	
	@Autowired
    private UserService userService;
	
	@Autowired
    private SongsRepositorio songsrepo;
	
	@GetMapping("/canciones/{idUser}")
    public ResponseEntity<?> getSongsUser(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        User currentUser = userService.findByUsername(username);
        List<Songs> songs = songsrepo.findByArtistaId(currentUser.getId());

        // Usar SongMapper en lugar de PostMapper
        List<SongDTO> songDtos = SongMapper.toDtoList(songs);

        return ResponseEntity.ok(songDtos);
    }
}
