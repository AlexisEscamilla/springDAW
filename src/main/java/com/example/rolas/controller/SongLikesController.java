package com.example.rolas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.rolas.Repositorio.SongLikesRepositorio;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.model.SongLikes;
import com.example.rolas.model.Songs;
import com.example.rolas.model.User;
import com.example.rolas.service.NotificationService;

@Controller
@RequestMapping("/songs")
public class SongLikesController {

    @Autowired
    private SongLikesRepositorio songLikesRepositorio;

    @Autowired
    private SongsRepositorio songsRepositorio;

    @Autowired
    private UserRepositorio userRepositorio;
    
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/{id}/like")
    @ResponseBody
    public String toggleLike(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        // Obtener el usuario autenticado
        User user = userRepositorio.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener la canción
        Songs song = songsRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Canción no encontrada"));

        // Verificar si el usuario ya dio like
        if (songLikesRepositorio.existsByUserAndSong(user, song)) {
            // Si ya dio like, eliminar el like
            SongLikes like = songLikesRepositorio.findByUserAndSong(user, song);
            song.setLikeCount(Math.max(0, song.getLikeCount() - 1));
            songLikesRepositorio.delete(like);
            return "unliked";
        } else {
            // Si no dio like, agregar un nuevo like
            song.setLikeCount(song.getLikeCount() + 1);
            SongLikes like = new SongLikes();
            like.setUser(user);
            like.setSong(song);
            songLikesRepositorio.save(like);

            // Crear una notificación para cada artista de la canción
            song.getSongArtists().forEach(artist -> {
                notificationService.createNotification(
                    artist.getArtista().getUser(), // Usuario que recibe la notificación (artista)
                    "like",           // Tipo de notificación
                    user,             // Usuario que dio like
                    song.getId()      // ID de la canción
                );
            });

            return "liked";
        }
    }
}