package com.example.rolas.controller;

import com.example.rolas.Repositorio.PlaylistSongsRepositorio;
import com.example.rolas.Repositorio.PlaylistsRepositorio;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.model.PlaylistSongs;
import com.example.rolas.model.Playlists;
import com.example.rolas.model.Songs;
import com.example.rolas.model.User;
import com.example.rolas.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class PlaylistsController {

    @Autowired
    private PlaylistsRepositorio playlistsRepositorio;
    
    @Autowired
    private PlaylistSongsRepositorio playlistSongsRepositorio;

    @Autowired
    private UserRepositorio userRepositorio;
    
    @Autowired
    private SongsRepositorio songRepositorio;

    @Autowired
    private NotificationService notificationService;
    
    @GetMapping("/subirPlaylist")
    public String mostrarFormularioSubirPlaylist() {
        return "subirPlaylist";
    }

    @PostMapping("/subirPlaylist")
    public String subirPlaylist(@RequestParam("title") String title,
                                @RequestParam("description") String descripcion,
                                @RequestParam("imagen") MultipartFile imagen,
                                @RequestParam("songIds") List<Integer> songIds,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        // Validar que el archivo no esté vacío
        if (imagen.isEmpty()) {
            model.addAttribute("error", "La imagen de la playlist es obligatoria.");
            return "subirPlaylist";
        }

        // Guardar la imagen en el sistema de archivos
        String imagenFileName = imagen.getOriginalFilename();
        Path imagenPath = Path.of("uploads", imagenFileName);

        try {
            // Crear la carpeta de uploads si no existe
            File uploadDir = new File("uploads");
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            // Guardar la imagen
            Files.copy(imagen.getInputStream(), imagenPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al guardar la imagen.");
            return "subirPlaylist";
        }

        // Obtener el usuario autenticado
        Optional<User> usuarioOpt = userRepositorio.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "Usuario no encontrado.");
            return "subirPlaylist";
        }

        User usuario = usuarioOpt.get();

        // Crear la playlist
        Playlists playlist = new Playlists();
        playlist.setTitle(title);
        playlist.setCoverImage(imagenPath.toString());
        playlist.setUser(usuario);
        playlist.setDescription(descripcion);
        playlist.setCreatedAt(LocalDate.now());

        // Guardar la playlist en la base de datos
        playlistsRepositorio.save(playlist);

        // Asociar las canciones seleccionadas a la playlist
     // ...existing code...
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

                    // --- Notificación al dueño de la canción ---
                 // Notificar a todos los artistas de la canción
                    song.getSongArtists().forEach(artist -> {
                        notificationService.createNotification(
                            artist.getArtista().getUser(), // Usuario que recibe la notificación (artista)
                            "playlist",                    // Tipo de notificación
                            usuario,                       // Quien agrega la canción
                            playlist.getId()               // ID de la playlist
                        );
                    });
                }
                System.out.println("Asociando canción con ID: " + songId + " a la playlist con ID: " + playlist.getId());
            }
        }
        
        return "redirect:/colecciones?tipo=playlists";
    }
    
    @PostMapping("/playlist/{id}/agregarCanciones")
    public String agregarCancionesAPlaylist(
            @PathVariable Integer id,
            @RequestParam(value = "songIds", required = false) List<Integer> songIds,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
    	User usuario = userRepositorio.findByUsername(userDetails.getUsername()).orElse(null);
        Optional<Playlists> playlistOpt = playlistsRepositorio.findById(id);
        if (playlistOpt.isEmpty() || songIds == null || songIds.isEmpty()) {
            return "redirect:/mostrarPlaylist/" + id;
        }
        Playlists playlist = playlistOpt.get();

        int posicion = playlist.getPlaylistSongs() != null ? playlist.getPlaylistSongs().size() + 1 : 1;
        for (Integer songId : songIds) {
            Optional<Songs> optionalSong = songRepositorio.findById(songId);
            if (optionalSong.isPresent()) {
                Songs song = optionalSong.get();
                // Elimina cualquier relación previa entre esta playlist y esta canción
                playlistSongsRepositorio.deleteByPlaylistIdAndSongId(id, songId);

                // Ahora sí, crea la nueva relación
                PlaylistSongs playlistSong = new PlaylistSongs();
                playlistSong.setPlaylist(playlist);
                playlistSong.setSong(song);
                playlistSong.setAddedAt(LocalDate.now());
                playlistSong.setPosicion(String.valueOf(posicion));
                posicion++;
                playlistSongsRepositorio.save(playlistSong);

                // --- Notificación al dueño de la canción ---
             // Notificar a todos los artistas de la canción
                song.getSongArtists().forEach(artist -> {
                    notificationService.createNotification(
                        artist.getArtista().getUser(), // Usuario que recibe la notificación (artista)
                        "playlist",                    // Tipo de notificación
                        usuario,                       // Quien agrega la canción
                        playlist.getId()               // ID de la playlist
                    );
                });
            }
        }
       
        return "redirect:/mostrarPlaylist/" + id;
    }

    // Eliminar una canción de la playlistaaaaaaaaaaaaa
    @PostMapping("/playlist/{playlistId}/eliminarCancion/{songId}")
    public String eliminarCancionDePlaylist(
            @PathVariable Integer playlistId,
            @PathVariable Integer songId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
    	playlistSongsRepositorio.deleteByPlaylistIdAndSongId(playlistId, songId);
        return "redirect:/mostrarPlaylist/" + playlistId;
    }

    // Eliminar la playlist completaaaaaaaaa
    @PostMapping("/playlist/{id}/eliminar")
    public String eliminarPlaylist(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Optional<Playlists> playlistOpt = playlistsRepositorio.findById(id);
        if (playlistOpt.isPresent()) {
            playlistsRepositorio.deleteById(id);
        }
        return "redirect:/colecciones?tipo=playlists";
    }
    
}