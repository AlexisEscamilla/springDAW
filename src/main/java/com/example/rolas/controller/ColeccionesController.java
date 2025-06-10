package com.example.rolas.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.rolas.Repositorio.AlbumsRepositorio;
import com.example.rolas.Repositorio.PlaylistSongsRepositorio;
import com.example.rolas.Repositorio.PlaylistsRepositorio;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.model.Albums;
import com.example.rolas.model.Artista;
import com.example.rolas.model.Songs;
import com.example.rolas.model.User;
import com.example.rolas.model.Playlists;
import java.util.List;

@Controller
public class ColeccionesController {
	
	@Autowired
    private UserRepositorio userRepositorio;

    @Autowired
    private SongsRepositorio songsRepositorio;

    @Autowired
    private AlbumsRepositorio albumsRepositorio;
    
    @Autowired
    private PlaylistsRepositorio playlistsRepositorio;
    
    @Autowired
    private PlaylistSongsRepositorio playlistSongsRepositorio;
	
    @GetMapping("/colecciones")
    public String mostrarColecciones(@AuthenticationPrincipal UserDetails userDetails,
                                     @RequestParam(value = "tipo", defaultValue = "canciones") String tipo,
                                     Model model) {
        Optional<User> usuarioOpt = userRepositorio.findByUsername(userDetails.getUsername());

        if (usuarioOpt.isPresent()) {
            User usuario = usuarioOpt.get();
            model.addAttribute("usuario", usuario);

            Artista artista = usuario.getArtista();
            boolean esArtista = (artista != null);
            model.addAttribute("esArtista", esArtista);
            
            if (esArtista) {
                // el artista  puede ver canciones, albums y playlists
                if (tipo.equals("canciones")) {
                    List<Songs> canciones = songsRepositorio.findByArtistaId(artista.getId());
                    model.addAttribute("canciones", canciones);
                } else if (tipo.equals("albums")) {
                    List<Albums> albums = albumsRepositorio.findByAlbumArtists_Artista(artista);
                    model.addAttribute("albums", albums);
                } else if (tipo.equals("playlists")) {
                    List<Playlists> playlists = playlistsRepositorio.findByUser(usuario);
                    playlists.forEach(playlist -> {
                        playlist.setPlaylistSongs(playlistSongsRepositorio.findByPlaylist(playlist));
                    });
                    model.addAttribute("playlists", playlists);
                }
            } else {
                // el oyente  solo puede ver y crear playlists
                tipo = "playlists";
                List<Playlists> playlists = playlistsRepositorio.findByUser(usuario);
                playlists.forEach(playlist -> {
                    playlist.setPlaylistSongs(playlistSongsRepositorio.findByPlaylist(playlist));
                });
                model.addAttribute("playlists", playlists);
            }
        } else {
            return "redirect:/feed";
        }

        model.addAttribute("tipo", tipo);
        return "colecciones";
    }
	
}
