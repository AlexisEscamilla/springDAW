package com.example.rolas.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rolas.DTO.AlbumDTO;
import com.example.rolas.DTO.AlbumMapper;
import com.example.rolas.Repositorio.AlbumsRepositorio;
import com.example.rolas.config.JwtUtil;
import com.example.rolas.model.Albums;
import com.example.rolas.model.User;
import com.example.rolas.service.UserService;

@RestController
@RequestMapping("/api")
public class AlbumsRestController {

	@Autowired
    private UserService userService;
	@Autowired
	private AlbumsRepositorio albumrepo;
	@GetMapping("/albums/{idUser}")
    public ResponseEntity<?> getalbumsUser(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        User currentUser = userService.findByUsername(username);
        List<Albums> albums = albumrepo.findByAlbumArtists_Artista(currentUser.getArtista());

        // Usar SongMapper en lugar de PostMapper
        List<AlbumDTO> albumDtos = AlbumMapper.toDtoList(albums);

        return ResponseEntity.ok(albumDtos);
    }
	
}
