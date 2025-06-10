package com.example.rolas.controller;

import com.example.rolas.model.Albums;
import com.example.rolas.model.Songs;
import com.example.rolas.model.AlbumArtists;
import com.example.rolas.model.Artista;
import com.example.rolas.model.User;
import com.example.rolas.Repositorio.AlbumArtistsRepositorio;
import com.example.rolas.Repositorio.AlbumsRepositorio;
import com.example.rolas.Repositorio.ArtistaRepositorio;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class AlbumController {

    @Autowired
    private AlbumsRepositorio albumRepositorio;
    
    @Autowired
    private SongsRepositorio songRepositorio;

    @Autowired
    private UserRepositorio userRepositorio;
    
    @Autowired
    private ArtistaRepositorio artistaRepositorio;
    
    @Autowired
    private AlbumArtistsRepositorio albumArtistRepositorio;


    @GetMapping("/subirAlbum")
    public String subirAlbum(@AuthenticationPrincipal UserDetails userDetails, Model model) {
    	User usuario = userRepositorio.findByUsername(userDetails.getUsername()).orElse(null);
        if (usuario == null || usuario.getArtista() == null) {
            return "redirect:/colecciones";
        }
    	return "subirAlbum";
    }

    @Transactional
    @PostMapping("/subirAlbum")
    public String saveAlbum(@RequestParam("title") String title,
                            @RequestParam("imagen") MultipartFile image,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        
        // Buscar al usuario autenticado (el artista)
        Optional<User> usuarioOpt = userRepositorio.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";  
        }

        User usuarioActual = usuarioOpt.get();
        Artista artista = usuarioActual.getArtista();

        // Verifica si el artista existe en la base de datos
        if (artista == null) {
            model.addAttribute("error", "El usuario no tiene un perfil de artista.");
            return "subirAlbum";
        }

        // se obtiene el id del artista desde la tabla artistas
        Integer artistaId = artista.getId();
        
        // Verifica si el artista existe en la tabla artistas por su id
        Optional<Artista> artistaOpt = artistaRepositorio.findById(artistaId);
        if (artistaOpt.isEmpty()) {
            model.addAttribute("error", "El artista con id " + artistaId + " no existe en la base de datos.");
            return "subirAlbum";
        }

        
        // guarda la imagen de portada
        String coverArtFileName = null;
        if (!image.isEmpty()) {
            try {
                
                String uploadDir = new File("uploads").getAbsolutePath();  
                new File(uploadDir).mkdirs();  
                coverArtFileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                File destino = new File(uploadDir + File.separator + coverArtFileName);
                image.transferTo(destino);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "Error al guardar la imagen del álbum.");
                return "subirAlbum";
            }
        }

        // crea un nuevo album
        Albums album = new Albums();
        album.setTitle(title);
        album.setCoverArt(coverArtFileName);  // guarda el nombre del archivo de la portada
        album.setReleaseDate(LocalDate.now()); 

        // guarda el album en la base de datos
        album = albumRepositorio.save(album);  

        // crea la relacion con el artista
        AlbumArtists albumArtist = new AlbumArtists();
        albumArtist.setAlbum(album);
        albumArtist.setArtista(artista); 
       
        albumArtistRepositorio.save(albumArtist);

        return "redirect:/colecciones";  // redirige a colecciones.html después de guardar el album
    }
    
    @PostMapping("/album/eliminar/{id}")
    public String eliminarAlbum(@PathVariable Integer id) {
        Albums album = albumRepositorio.findById(id).orElseThrow();

        // busca todas las canciones de ese album
        List<Songs> canciones = songRepositorio.findByAlbumId(id);

        // elimina archivos de las canciones
        for (Songs song : canciones) {
            // elimina archivo de audio
            if (song.getFileUrl() != null) {
                try {
                    Files.deleteIfExists(Paths.get(song.getFileUrl()));
                } catch (Exception e) {
                   System.out.println("error");
                }
            }
            // elimina portada de la canción si aplica
            if (song.getCoverUrl() != null) {
                try {
                    Files.deleteIfExists(Paths.get("uploads/" + song.getCoverUrl()));
                } catch (Exception e) {
                	System.out.println("error");
                }
            }
        }

        //elimina portada del album si aplica
        if (album.getCoverArt() != null) {
            try {
                Files.deleteIfExists(Paths.get("uploads/" + album.getCoverArt()));
            } catch (Exception e) {
            	System.out.println("error");
            }
        }

        //  elimina las canciones
        songRepositorio.deleteAll(canciones);

        // elimina el álbum
        albumRepositorio.delete(album);

        return "redirect:/colecciones?tipo=albumes";
    }

}


