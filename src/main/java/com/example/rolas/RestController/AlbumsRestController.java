package com.example.rolas.RestController;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.rolas.DTO.AlbumDTO;
import com.example.rolas.DTO.AlbumMapper;
import com.example.rolas.Repositorio.AlbumArtistsRepositorio;
import com.example.rolas.Repositorio.AlbumsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.config.JwtUtil;
import com.example.rolas.model.AlbumArtists;
import com.example.rolas.model.Albums;
import com.example.rolas.model.Artista;
import com.example.rolas.model.User;
import com.example.rolas.service.UserService;

import io.jsonwebtoken.io.IOException;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api")
public class AlbumsRestController {

	@Autowired
    private UserService userService;
	@Autowired
	private AlbumsRepositorio albumRepositorio;
	
	@Autowired
    private AlbumArtistsRepositorio albumArtistRepositorio;
	
	@Autowired
    private UserRepositorio userRepositorio;
	
	@GetMapping("/albums/{idUser}")
    public ResponseEntity<?> getalbumsUser(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        User currentUser = userService.findByUsername(username);
        List<Albums> albums = albumRepositorio.findByAlbumArtists_Artista(currentUser.getArtista());

        // Usar SongMapper en lugar de PostMapper
        List<AlbumDTO> albumDtos = AlbumMapper.toDtoList(albums);

        return ResponseEntity.ok(albumDtos);
    }
	
	@Transactional
	@PostMapping("/albums")
	public ResponseEntity<?> saveAlbum(
	        @RequestParam("title") String title,
	        @RequestParam("imagen") MultipartFile image,
	        @RequestHeader("Authorization") String token) throws IllegalStateException, java.io.IOException {

	    try {
	        // Obtener usuario desde token JWT
	        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
	        Optional<User> usuarioOpt = userRepositorio.findByUsername(username);
	        if (usuarioOpt.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no encontrado"));
	        }

	        User usuarioActual = usuarioOpt.get();
	        Artista artista = usuarioActual.getArtista();

	        if (artista == null) {
	            return ResponseEntity.badRequest().body(Map.of("error", "El usuario no tiene un perfil de artista"));
	        }

	        // guardar imagen
	        String coverArtFileName = null;
	        if (!image.isEmpty()) {
	            String uploadDir = new File("uploads").getAbsolutePath();
	            new File(uploadDir).mkdirs();
	            coverArtFileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
	            File destino = new File(uploadDir + File.separator + coverArtFileName);
	            image.transferTo(destino);
	        }

	        // crear álbum
	        Albums album = new Albums();
	        album.setTitle(title);
	        album.setCoverArt(coverArtFileName);
	        album.setReleaseDate(LocalDate.now());
	        album = albumRepositorio.save(album);

	        // vincular con artista
	        AlbumArtists albumArtist = new AlbumArtists();
	        albumArtist.setAlbum(album);
	        albumArtist.setArtista(artista);
	        albumArtistRepositorio.save(albumArtist);

	        return ResponseEntity.ok(Map.of(
	                "message", "Álbum creado con éxito",
	                "albumId", album.getId(),
	                "coverArt", coverArtFileName
	        ));

	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("error", "Error al guardar la imagen del álbum"));
	    }
	}

	
}
