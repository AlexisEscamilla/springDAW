package com.example.rolas.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.rolas.DTO.PlaylistsDTO;
import com.example.rolas.DTO.PlaylistMapper;
import com.example.rolas.Repositorio.PlaylistSongsRepositorio;
import com.example.rolas.Repositorio.PlaylistsRepositorio;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.config.JwtUtil;
import com.example.rolas.model.PlaylistSongs;
import com.example.rolas.model.Playlists;
import com.example.rolas.model.SongArtists;
import com.example.rolas.model.Songs;
import com.example.rolas.model.User;
import com.example.rolas.service.NotificationService;
import com.example.rolas.service.UserService;

import io.jsonwebtoken.io.IOException;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api")
public class PlaylistsRestController {

	@Autowired
    private UserService userService;
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
    private PlaylistsRepositorio playlistsRepositorio;
    
    @Autowired
    private PlaylistSongsRepositorio playlistSongsRepositorio;

    @Autowired
    private UserRepositorio userRepositorio;
    
    @Autowired
    private SongsRepositorio songRepositorio;

	@GetMapping("/playlists/{idUser}")
    public ResponseEntity<?> getPlaylistsUser(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        User currentUser = userService.findByUsername(username);
        List<Playlists> playlists = playlistsRepositorio.findByUser(currentUser);

        // Usar SongMapper en lugar de PostMapper
        List<PlaylistsDTO> playlistsDTOs = PlaylistMapper.toDtoList(playlists);

        return ResponseEntity.ok(playlistsDTOs);
    }
	
	@PostMapping("/playlists")
	@Transactional
	public ResponseEntity<?> subirPlaylist(
	        @RequestParam("title") String title,
	        @RequestParam("description") String descripcion,
	        @RequestParam("imagen") MultipartFile imagen,
	        @RequestParam(value = "songIds", required = false) List<Integer> songIds,
	        @RequestHeader("Authorization") String token) throws java.io.IOException {

	    try {
	        // Validar imagen
	        if (imagen.isEmpty()) {
	            return ResponseEntity.badRequest().body(Map.of("error", "La imagen de la playlist es obligatoria"));
	        }

	        // Guardar imagen
	        String imagenFileName = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
	        File uploadDir = new File("uploads");
	        if (!uploadDir.exists()) uploadDir.mkdirs();
	        Path imagenPath = Path.of(uploadDir.getAbsolutePath(), imagenFileName);
	        Files.copy(imagen.getInputStream(), imagenPath, StandardCopyOption.REPLACE_EXISTING);

	        // Obtener usuario desde token
	        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
	        Optional<User> usuarioOpt = userRepositorio.findByUsername(username);
	        if (usuarioOpt.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no encontrado"));
	        }
	        User usuario = usuarioOpt.get();

	        // Crear playlist
	        Playlists playlist = new Playlists();
	        playlist.setTitle(title);
	        playlist.setCoverImage(imagenFileName);
	        playlist.setUser(usuario);
	        playlist.setDescription(descripcion);
	        playlist.setCreatedAt(LocalDate.now());
	        playlistsRepositorio.save(playlist);

	        // Asociar canciones si vienen
	        if (songIds != null && !songIds.isEmpty()) {
	            int posicion = 1;
	            for (Integer songId : songIds) {
	                Optional<Songs> optionalSong = songRepositorio.findById(songId);
	                if (optionalSong.isPresent()) {
	                    Songs song = optionalSong.get();

	                    PlaylistSongs playlistSong = new PlaylistSongs();
	                    playlistSong.setPlaylist(playlist);
	                    playlistSong.setSong(song);
	                    playlistSong.setAddedAt(LocalDate.now());
	                    playlistSong.setPosicion(String.valueOf(posicion));
	                    posicion++;
	                    playlistSongsRepositorio.save(playlistSong);

	                    // Notificar a los artistas de la canción (usar bucle tradicional)
	                    for (SongArtists artist : song.getSongArtists()) {
	                        notificationService.createNotification(
	                                artist.getArtista().getUser(),
	                                "playlist",
	                                usuario,
	                                playlist.getId()
	                        );
	                    }
	                }
	            }
	        }

	        return ResponseEntity.ok(Map.of(
	                "message", "Playlist creada con éxito",
	                "playlistId", playlist.getId(),
	                "coverImage", imagenFileName
	        ));

	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("error", "Error al guardar la imagen de la playlist"));
	    }
	}


	@PostMapping("/playlists/{id}/agregarCanciones")
	@Transactional
	public ResponseEntity<?> agregarCancionesAPlaylist(
	        @PathVariable Integer id,
	        @RequestParam(value = "songIds", required = false) List<Integer> songIds,
	        @RequestHeader("Authorization") String token) {

	    try {
	        if (songIds == null || songIds.isEmpty()) {
	            return ResponseEntity.badRequest().body(Map.of("error", "Debes enviar al menos una canción para agregar"));
	        }

	        // Usuario autenticado
	        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
	        User usuario = userRepositorio.findByUsername(username).orElse(null);
	        if (usuario == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no encontrado"));
	        }

	        // Buscar playlist
	        Optional<Playlists> playlistOpt = playlistsRepositorio.findById(id);
	        if (playlistOpt.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Playlist no encontrada"));
	        }
	        Playlists playlist = playlistOpt.get();

	        // Determinar posición inicial (si ya tiene canciones)
	        int posicion = (playlist.getPlaylistSongs() != null) ? playlist.getPlaylistSongs().size() + 1 : 1;

	        // Agregar canciones
	        for (Integer songId : songIds) {
	            Optional<Songs> optionalSong = songRepositorio.findById(songId);
	            if (optionalSong.isPresent()) {
	                Songs song = optionalSong.get();

	                // eliminar relacion previa entre esta playlist y esta canción
	                playlistSongsRepositorio.deleteByPlaylistIdAndSongId(id, songId);

	                // crear la nueva relación
	                PlaylistSongs playlistSong = new PlaylistSongs();
	                playlistSong.setPlaylist(playlist);
	                playlistSong.setSong(song);
	                playlistSong.setAddedAt(LocalDate.now());
	                playlistSong.setPosicion(String.valueOf(posicion));
	                posicion++;
	                playlistSongsRepositorio.save(playlistSong);

	                // Notificar a los artistas de la canción
	                for (SongArtists artist : song.getSongArtists()) {
	                    notificationService.createNotification(
	                            artist.getArtista().getUser(),
	                            "playlist",
	                            usuario,
	                            playlist.getId()
	                    );
	                }
	            }
	        }

	        return ResponseEntity.ok(Map.of(
	                "message", "Canciones agregadas correctamente a la playlist",
	                "playlistId", playlist.getId()
	        ));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
	                "error", "Error al agregar canciones a la playlist"
	        ));
	    }
	}



}
