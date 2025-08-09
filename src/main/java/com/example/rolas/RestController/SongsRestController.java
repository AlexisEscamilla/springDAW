package com.example.rolas.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

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

import com.example.rolas.DTO.SongDTO;
import com.example.rolas.DTO.SongMapper;
import com.example.rolas.Repositorio.AlbumsRepositorio;
import com.example.rolas.Repositorio.SongArtistsRepositorio;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.config.JwtUtil;
import com.example.rolas.model.Albums;
import com.example.rolas.model.Artista;
import com.example.rolas.model.SongArtists;
import com.example.rolas.model.Songs;
import com.example.rolas.model.User;
import com.example.rolas.service.UserService;

import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/api")
public class SongsRestController {

	
	@Autowired
    private UserService userService;
	
	@Autowired
    private SongsRepositorio songRepositorio;
    
    @Autowired
    private SongArtistsRepositorio songArtistsRepositorio;
    
    @Autowired
    private AlbumsRepositorio albumRepositorio;
    
    @Autowired
    private UserRepositorio userRepositorio;
	
	@GetMapping("/canciones/{idUser}")
    public ResponseEntity<?> getSongsUser(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        User currentUser = userService.findByUsername(username);
        List<Songs> songs = songRepositorio.findByArtistaId(currentUser.getId());

        // Usar SongMapper en lugar de PostMapper
        List<SongDTO> songDtos = SongMapper.toDtoList(songs);

        return ResponseEntity.ok(songDtos);
    }
	
	@PostMapping("/canciones")
	public ResponseEntity<?> subirCancionAPI(
	        @RequestHeader("Authorization") String token,
	        @RequestParam("title") String title,
	        @RequestParam("duration") String duration,
	        @RequestParam("audio") MultipartFile audioFile,
	        @RequestParam("cover") MultipartFile coverFile,
	        @RequestParam("genero") String genero,
	        @RequestParam("album") Integer albumId,
	        @RequestParam(value = "artists", required = false) String artistsString
	) {
	    try {
	        // 1. Usuario autenticado
	        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
	        User user = userService.findByUsername(username);
	        if (user == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
	        }

	        // 2. Validar archivos
	        if (audioFile.isEmpty() || coverFile.isEmpty()) {
	            return ResponseEntity.badRequest().body("Los archivos de audio y portada son obligatorios.");
	        }

	        // 3. Crear carpeta uploads si no existe
	        File uploadDir = new File("uploads");
	        if (!uploadDir.exists()) {
	            uploadDir.mkdir();
	        }

	        // 4. Guardar audio
	        String audioFileName = System.currentTimeMillis() + "_" + audioFile.getOriginalFilename();
	        Path audioPath = Path.of("uploads", audioFileName);
	        Files.copy(audioFile.getInputStream(), audioPath, StandardCopyOption.REPLACE_EXISTING);

	        // 5. Guardar portada
	        String coverFileName = System.currentTimeMillis() + "_" + coverFile.getOriginalFilename();
	        Path coverPath = Path.of("uploads", coverFileName);
	        Files.copy(coverFile.getInputStream(), coverPath, StandardCopyOption.REPLACE_EXISTING);

	        // 6. Crear canción
	        Songs song = new Songs();
	        song.setTitle(title);
	        song.setDuration(duration);
	        song.setFileUrl(audioFileName);
	        song.setCoverUrl(coverFileName);
	        song.setGender(genero);

	        Albums album = albumRepositorio.findById(albumId)
	                .orElseThrow(() -> new RuntimeException("Álbum no encontrado"));
	        song.setAlbum(album);
	        songRepositorio.save(song);

	        // 7. Relación artista principal
	        Artista artistaPrincipal = user.getArtista();
	        if (artistaPrincipal == null) {
	            return ResponseEntity.badRequest().body("El usuario autenticado no es un artista.");
	        }

	        SongArtists songArtistPrincipal = new SongArtists();
	        songArtistPrincipal.setSong(song);
	        songArtistPrincipal.setArtista(artistaPrincipal);
	        songArtistsRepositorio.save(songArtistPrincipal);

	        // 8. Artistas adicionales
	        if (artistsString != null && !artistsString.isBlank()) {
	            String[] usernames = artistsString.split(",");
	            for (String usernameExtraRaw : usernames) {
	                String usernameExtra = usernameExtraRaw.trim();
	                if (usernameExtra.isEmpty()) continue;
	                if (user.getUsername().equals(usernameExtra)) continue;

	                User additionalUser = userRepositorio.findByUsername(usernameExtra)
	                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usernameExtra));
	                Artista additionalArtista = additionalUser.getArtista();
	                if (additionalArtista == null) {
	                    return ResponseEntity.badRequest().body("El usuario " + usernameExtra + " no es un artista.");
	                }
	                SongArtists songArtist = new SongArtists();
	                songArtist.setSong(song);
	                songArtist.setArtista(additionalArtista);
	                songArtistsRepositorio.save(songArtist);
	            }

	        }

	        return ResponseEntity.ok("Canción subida correctamente");

	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error al guardar los archivos: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error: " + e.getMessage());
	    }
	}


}
