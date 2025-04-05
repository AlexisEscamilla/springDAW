package com.example.rolas.controller;

import com.example.rolas.model.Albums;
import com.example.rolas.model.AlbumArtists;
import com.example.rolas.model.Artista;
import com.example.rolas.model.User;
import com.example.rolas.Repositorio.AlbumArtistsRepositorio;
import com.example.rolas.Repositorio.AlbumsRepositorio;
import com.example.rolas.Repositorio.ArtistaRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import jakarta.servlet.http.HttpServletRequest;
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
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
public class AlbumController {

    @Autowired
    private AlbumsRepositorio albumRepositorio;

    @Autowired
    private UserRepositorio userRepositorio;
    
    @Autowired
    private ArtistaRepositorio artistaRepositorio;
    
    @Autowired
    private AlbumArtistsRepositorio albumArtistRepositorio;


    @GetMapping("/subirAlbum")
    public String subirAlbum() {
        return "subirAlbum";  // El formulario HTML para subir el álbum
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

        // Verificar si el artista existe en la base de datos
        if (artista == null) {
            model.addAttribute("error", "El usuario no tiene un perfil de artista.");
            return "subirAlbum";
        }

        // Obtener el ID del artista desde la tabla `artistas`
        Integer artistaId = artista.getId();  // Aquí usamos el campo `id` de la tabla `artistas`
        
        System.out.println("PERRRO ESSS" + artista.getId());
        
        // Verifica si el artista existe en la tabla `artistas` por su `id`
        Optional<Artista> artistaOpt = artistaRepositorio.findById(artistaId);
        if (artistaOpt.isEmpty()) {
            model.addAttribute("error", "El artista con id " + artistaId + " no existe en la base de datos.");
            return "subirAlbum";
        }

        
        // Guardar la imagen de portada
        String coverArtFileName = null;
        if (!image.isEmpty()) {
            try {
                // Guardar la imagen en el servidor
                String uploadDir = new File("uploads").getAbsolutePath();  // Carpeta "uploads"
                new File(uploadDir).mkdirs();  // Crear carpeta si no existe
                coverArtFileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                File destino = new File(uploadDir + File.separator + coverArtFileName);
                image.transferTo(destino);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "Error al guardar la imagen del álbum.");
                return "subirAlbum";
            }
        }

     // Crear un nuevo álbum
        Albums album = new Albums();
        album.setTitle(title);
        album.setCoverArt(coverArtFileName);  // Guardar el nombre del archivo de la portada
        album.setReleaseDate(LocalDate.now());  // Fecha de creación del álbum

        // Guardar el álbum en la base de datos
        album = albumRepositorio.save(album);  // Guardamos el álbum y obtenemos el ID generado

        // Crear la relación con el artista
        AlbumArtists albumArtist = new AlbumArtists();
        albumArtist.setAlbum(album);
        albumArtist.setArtista(artista);  // Asociar el artista con el álbum

        // Guardar la relación en la base de datos
        albumArtistRepositorio.save(albumArtist);

        // Redirigir a la página de colecciones después de guardar el álbum
        return "redirect:/colecciones";  // Redirige a colecciones.html después de guardar el álbum
    }

}


