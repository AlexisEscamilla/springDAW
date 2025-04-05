package com.example.rolas.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.example.rolas.model.Songs;
import com.example.rolas.model.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.example.rolas.model.Albums;
import com.example.rolas.model.Artista;
import com.example.rolas.model.SongArtists;
import com.example.rolas.Repositorio.AlbumArtistsRepositorio;
import com.example.rolas.Repositorio.AlbumsRepositorio;
import com.example.rolas.Repositorio.ArtistaRepositorio;
import com.example.rolas.Repositorio.SongArtistsRepositorio;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;

import org.springframework.ui.Model;

@Controller
public class SongsController {

    @Autowired
    private ArtistaRepositorio artistaRepositorio;

    @Autowired
    private AlbumsRepositorio albumRepositorio;
    
    @Autowired
    private AlbumArtistsRepositorio albumArtistRepositorio;
    
    @Autowired
    private UserRepositorio userRepositorio;

    @Autowired
    private SongsRepositorio songRepositorio;
    
    @Autowired
    private SongArtistsRepositorio songArtistsRepositorio;

    @GetMapping("/subirCancion")
    public String subirCancionForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        // Obtener todos los álbumes del repositorio
        List<Albums> albums = albumRepositorio.findAll(); // Cambiado para obtener todos los álbumes

        // Poner los álbumes en el modelo para que estén disponibles en el formulario
        model.addAttribute("albums", albums);

        return "subirCancion";  // nombre del archivo HTML de la vista
    }


    @PostMapping("/subirCancion")
    public String subirCancion(@RequestParam("title") String title,
					            @RequestParam("duration") String duration,
					            @RequestParam("audio") MultipartFile audioFile,  // Archivo de la canción
					            @RequestParam("cover") MultipartFile coverFile,  // Archivo de la portada
					            @RequestParam("genero") String genero,
					            @RequestParam("album") Integer albumId,
					            @RequestParam(value = "artist2", required = false) List<String> artistUsernames,  // Lista de usernames de artistas
					            @AuthenticationPrincipal UserDetails userDetails,
					            Model model) {
	    	
    	
    	// Validar que los archivos no estén vacíos
        if (audioFile.isEmpty() || coverFile.isEmpty()) {
            model.addAttribute("error", "Los archivos de audio y portada son obligatorios.");
            return "subirCancion";
        }

        // Guardar los archivos en el sistema de archivos
        String audioFileName = audioFile.getOriginalFilename();
        String coverFileName = coverFile.getOriginalFilename();
        Path audioPath = Path.of("uploads", audioFileName);
        Path coverPath = Path.of("uploads", coverFileName);

        
        try {
            // Crear la carpeta de uploads si no existe
            File uploadDir = new File("uploads");
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            // Guardar los archivos
            Files.copy(audioFile.getInputStream(), audioPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(coverFile.getInputStream(), coverPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al guardar los archivos.");
            return "subirCancion";
        }

        // Crear la canción
        Songs song = new Songs();
        song.setTitle(title);
        song.setDuration(duration);
        song.setFileUrl(audioPath.toString());
        song.setCoverUrl(coverPath.toString());
        song.setGender(genero);

        // Relacionar la canción con el álbum
        Albums album = albumRepositorio.findById(albumId)
            .orElseThrow(() -> new RuntimeException("Álbum no encontrado"));
        song.setAlbum(album);

        songRepositorio.save(song);

        
     // Obtener el usuario autenticado
        User user = userRepositorio.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si el usuario autenticado es un artista
        Artista artistaPrincipal = user.getArtista();
        if (artistaPrincipal == null) {
            model.addAttribute("error", "El usuario autenticado no es un artista.");
            return "subirCancion";
        }

        // Crear la relación en song_artists para el artista principal
        SongArtists songArtistPrincipal = new SongArtists();
        songArtistPrincipal.setSong(song);
        songArtistPrincipal.setArtista(artistaPrincipal);
        songArtistsRepositorio.save(songArtistPrincipal);

        // Procesar los artistas adicionales ingresados
        if (artistUsernames != null && !artistUsernames.isEmpty()) {
            for (String username : artistUsernames) {
                // Buscar el usuario por username
                User additionalUser = userRepositorio.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

                // Verificar si el usuario es un artista
                Artista additionalArtista = additionalUser.getArtista();
                if (additionalArtista == null) {
                    model.addAttribute("error", "El usuario " + username + " no es un artista.");
                    return "subirCancion";
                }

                // Crear la relación en song_artists
                SongArtists songArtist = new SongArtists();
                songArtist.setSong(song);
                songArtist.setArtista(additionalArtista);
                songArtistsRepositorio.save(songArtist);
            }
        }

        // Redirigir a la página de canciones o de confirmación
        return "redirect:/colecciones";  // Asegúrate de tener la ruta correcta
    }
    


}
