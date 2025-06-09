package com.example.rolas.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.rolas.Repositorio.AlbumArtistsRepositorio;
import com.example.rolas.Repositorio.AlbumsRepositorio;
import com.example.rolas.Repositorio.FollowersRepositorio;
import com.example.rolas.Repositorio.PlaylistsRepositorio;
import com.example.rolas.Repositorio.PostLikesRepositorio;
import com.example.rolas.Repositorio.PostRepositorio;
import com.example.rolas.Repositorio.SongsRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.model.SongComments;
import com.example.rolas.model.Songs;
import com.example.rolas.model.User;
import com.example.rolas.service.PostCommentsService;
import com.example.rolas.service.PostService;
import com.example.rolas.model.AlbumArtists;
import com.example.rolas.model.Albums;
import com.example.rolas.model.Artista;
import com.example.rolas.model.Followers;
import com.example.rolas.model.Playlists;
import com.example.rolas.model.Post;
import com.example.rolas.model.PostComments;


@Controller
public class MostrarPerfilController {

    @Autowired
    private UserRepositorio userRepositorio;	
    
    @Autowired
    private SongsRepositorio songsRepositorio;	
    
    @Autowired
    private FollowersRepositorio followersrepositorio;
    
    @Autowired
    private PlaylistsRepositorio playlistsrepositorio;
    
    @Autowired
    private AlbumArtistsRepositorio albumArtistsRepositorio;
    
    @Autowired
    private AlbumsRepositorio albumsRepositorio;
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private PostRepositorio postRepositorio;
    
    @Autowired
    private PostCommentsService postCommentsService;
    
    @Autowired
    private PostLikesRepositorio postLikesRepository;

    @GetMapping("/mostrarPerfil/{id}")
    public String mostrarPerfil(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Obtener el usuario por ID
        User usuario = userRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener el usuario autenticadoaaaaa
        User usuarioAutenticado = userRepositorio.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        List<Post> posts = postRepositorio.findByUserIdOrderByCreatedAtDesc(usuario.getId());
        List<PostComments> allComments = postCommentsService.getAllCommentsForPosts(posts);
        
        for (Post post : posts) {
            boolean liked = postLikesRepository.existsByUserAndPost(usuarioAutenticado, post);
            post.setLikedByCurrentUser(liked); // Debes agregar este campo y su getter/setter en tu modelo Post
        }
        
        System.out.println("Comentarios:");
        allComments.forEach(comment -> {
            if (comment != null) {
                System.out.println("Comentario ID: " + comment.getId() + ", Texto: " + comment.getCommentText());
            } else {
                System.out.println("Comentario nulo encontrado");
            }
        });
        //aaaaaaaa
        Map<Integer, Integer> postCommentCounts = new HashMap<>();
        for (Post post : posts) {
            int count = (int) allComments.stream().filter(c -> c.getPost().getId().equals(post.getId())).count();
            postCommentCounts.put(post.getId(), count);
        }
        
        // Verificar si el usuario autenticado ya sigue al usuario del perfil
        boolean isFollowing = followersrepositorio.existsByFollowerAndFollowing(usuarioAutenticado, usuario);

        // Seguidores y siguiendo
        List<Followers> seguidores = followersrepositorio.findByFollower(usuario);
        List<Followers> siguiendo = followersrepositorio.findByFollowing(usuario);

        // Si es artista, obtiene canciones, si no, pasa lista vacíaaaa
        Artista artista = usuario.getArtista();
        List<Songs> canciones = (artista != null) ? songsRepositorio.findByArtistaId(artista.getId()) : List.of();
        int cancionesCount = canciones.size();
        // Obtener todas las playlists del usuarioa
        List<Playlists> playlists = playlistsrepositorio.findByUser(usuario);

        List<Albums> albums = List.of();
        if (artista != null) {
            List<AlbumArtists> albumArtists = albumArtistsRepositorio.findByArtistaId(artista.getId());
            albums = albumArtists.stream()
                    .map(AlbumArtists::getAlbum)
                    .distinct()
                    .toList();
        }
        
        System.out.println(albums.size());
        model.addAttribute("cancionesCount", cancionesCount);
        model.addAttribute("albums", albums);
        model.addAttribute("playlists", playlists);
        model.addAttribute("usuario", usuario);
        model.addAttribute("seguidores", seguidores);
        model.addAttribute("siguiendo", siguiendo);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("canciones", canciones);
        model.addAttribute("esArtista", artista != null);
        model.addAttribute("postCommentCounts", postCommentCounts);
        model.addAttribute("posts", posts);
        model.addAttribute("comments", allComments);
        
        return "mostrarPerfil";
    }
}
