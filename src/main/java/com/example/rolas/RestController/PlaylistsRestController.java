package com.example.rolas.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rolas.DTO.PlaylistsDTO;
import com.example.rolas.DTO.PlaylistMapper;
import com.example.rolas.Repositorio.PlaylistsRepositorio;
import com.example.rolas.config.JwtUtil;
import com.example.rolas.model.Playlists;
import com.example.rolas.model.User;
import com.example.rolas.service.UserService;

@RestController
@RequestMapping("/api")
public class PlaylistsRestController {

	@Autowired
    private UserService userService;
	@Autowired
	private PlaylistsRepositorio playlistrepo;
	@GetMapping("/playlists/{idUser}")
    public ResponseEntity<?> getPlaylistsUser(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        User currentUser = userService.findByUsername(username);
        List<Playlists> playlists = playlistrepo.findByUser(currentUser);

        // Usar SongMapper en lugar de PostMapper
        List<PlaylistsDTO> playlistsDTOs = PlaylistMapper.toDtoList(playlists);

        return ResponseEntity.ok(playlistsDTOs);
    }
}
