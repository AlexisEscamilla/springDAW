package com.example.rolas.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.example.rolas.model.Songs;
import com.example.rolas.model.User;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.example.rolas.model.AlbumArtists;
import com.example.rolas.model.Albums;
import com.example.rolas.model.Artista;
import com.example.rolas.model.SongArtists;
import com.example.rolas.Repositorio.AlbumArtistsRepositorio;
import com.example.rolas.Repositorio.AlbumsRepositorio;
import com.example.rolas.Repositorio.PlaylistSongsRepositorio;
import com.example.rolas.Repositorio.SongArtistsRepositorio;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;

import org.springframework.ui.Model;

@Controller
public class SongsController {

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
    
    @Autowired
    private PlaylistSongsRepositorio  playlistSongsRepositorio;

    @GetMapping("/subirCancion")
    public String subirCancionForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
    	User user = userRepositorio.findByUsername(userDetails.getUsername()).orElse(null);
    	if (user == null || user.getArtista() == null) {
            // redirige a colecciones o muestra error si NO es artista
            return "redirect:/colecciones";
        }
        // obtiene todos los albumes del repositorio
        List<Albums> albums = albumRepositorio.findByAlbumArtists_Artista(user.getArtista()); 

        // pone los albumes en el modelo para que esten disponibles en el formulario
        model.addAttribute("albums", albums);

        return "subirCancion";  
    }


    @PostMapping("/subirCancion")
    public String subirCancion(
    	    @RequestParam("title") String title,
    	    @RequestParam("duration") String duration,
    	    @RequestParam("audio") MultipartFile audioFile,
    	    @RequestParam("cover") MultipartFile coverFile,
    	    @RequestParam("genero") String genero,
    	    @RequestParam("album") Integer albumId,
    	    @RequestParam(value = "artists", required = false) String artistsString, 
    	    @AuthenticationPrincipal UserDetails userDetails,
    	    Model model
    	) {
    	
    	
	    	
    	User user = userRepositorio.findByUsername(userDetails.getUsername()).orElse(null);
        
    	// valida que los archivos no estén vacios
        if (audioFile.isEmpty() || coverFile.isEmpty()) {
            model.addAttribute("error", "Los archivos de audio y portada son obligatorios.");
            return "subirCancion";
        }

        try {
            // crea la carpeta de uploads si no existe
            File uploadDir = new File("uploads");
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            // guarda archivo de audio
            String audioFileName = System.currentTimeMillis() + "_" + audioFile.getOriginalFilename();
            Path audioPath = Path.of("uploads", audioFileName);
            Files.copy(audioFile.getInputStream(), audioPath, StandardCopyOption.REPLACE_EXISTING);

            // guarda archivo de portada
            String coverFileName = System.currentTimeMillis() + "_" + coverFile.getOriginalFilename();
            Path coverPath = Path.of("uploads", coverFileName);
            Files.copy(coverFile.getInputStream(), coverPath, StandardCopyOption.REPLACE_EXISTING);
            
            
            // crea la cancion
            Songs song = new Songs();
            song.setTitle(title);
            song.setDuration(duration);
            song.setFileUrl(audioFileName);      
            song.setCoverUrl(coverFileName);     
            song.setGender(genero);
            
         // relaciona la cancion con el album
            Albums album = albumRepositorio.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Álbum no encontrado"));
            song.setAlbum(album);

            songRepositorio.save(song);

            // verifica si el usuario autenticado es un artista
            Artista artistaPrincipal = user.getArtista();
            if (artistaPrincipal == null) {
                model.addAttribute("error", "El usuario autenticado no es un artista.");
                return "subirCancion";
            }

            // crea la relacion en song_artists para el artista principal
            SongArtists songArtistPrincipal = new SongArtists();
            songArtistPrincipal.setSong(song);
            songArtistPrincipal.setArtista(artistaPrincipal);
            songArtistsRepositorio.save(songArtistPrincipal);

            // procesa los artistas adicionales ingresados
            if (artistsString != null && !artistsString.isBlank()) {
                String[] usernames = artistsString.split(",");
                for (String username : usernames) {
                    username = username.trim();
                    if (username.isEmpty()) continue;
                    // evita duplicar al artista principal
                    if (user.getUsername().equals(username)) continue;

                    User additionalUser = userRepositorio.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado: "));
                    Artista additionalArtista = additionalUser.getArtista();
                    if (additionalArtista == null) {
                        model.addAttribute("error", "El usuario " + username + " no es un artista.");
                        return "subirCancion";
                    }
                    SongArtists songArtist = new SongArtists();
                    songArtist.setSong(song);
                    songArtist.setArtista(additionalArtista);
                    songArtistsRepositorio.save(songArtist);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al guardar los archivos.");
            return "subirCancion";
        }

       
        return "redirect:/colecciones"; 
    }
    
    @GetMapping("/buscarCanciones")
    @ResponseBody
    public List<Map<String, String>> buscarCanciones(@RequestParam("query") String query) {
        List<Songs> canciones = songRepositorio.findByTitleContainingIgnoreCase(query);
        List<Map<String, String>> resultados = new ArrayList<>();
        for (Songs cancion : canciones) {
            Map<String, String> resultado = new HashMap<>();
            resultado.put("id", cancion.getId().toString());
            resultado.put("title", cancion.getTitle());

            Optional<AlbumArtists> albumArtist = albumArtistRepositorio.findByAlbum(cancion.getAlbum());
            if (albumArtist.isPresent()) {
                resultado.put("artist", albumArtist.get().getArtista().getUser().getNombre());
            } else {
                resultado.put("artist", "Desconocido");
            }

            resultados.add(resultado);
        }
        return resultados;
    }
    
    @PostMapping("/cancion/eliminar/{id}")
    public String eliminarCancion(@PathVariable Integer id) {
        Songs cancion = songRepositorio.findById(id).orElseThrow();

        // elimina archivo de audio
        if (cancion.getFileUrl() != null) {
            try {
                Files.deleteIfExists(Paths.get(cancion.getFileUrl()));
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
        // elimina portada si aplica
        if (cancion.getCoverUrl() != null) {
            try {
                Files.deleteIfExists(Paths.get("uploads/" + cancion.getCoverUrl()));
            } catch (Exception e) {
            	System.out.println("Error");
            }
        }
        playlistSongsRepositorio.deleteBySongId(id);

       
        songRepositorio.delete(cancion);

        return "redirect:/colecciones?tipo=canciones";
    }

}
