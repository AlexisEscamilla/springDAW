package com.example.rolas.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rolas.Repositorio.PostLikesRepositorio;
import com.example.rolas.config.JwtUtil;
import com.example.rolas.model.Post;
import com.example.rolas.model.PostComments;
import com.example.rolas.model.User;
import com.example.rolas.service.PostCommentsService;
import com.example.rolas.service.PostService;
import com.example.rolas.service.UserService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/feed")
public class FeedRestController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private PostCommentsService postCommentsService;
    
    @Autowired
    private PostLikesRepositorio postLikesRepository;

    @GetMapping("/posts")
    public ResponseEntity<?> getFeed(@RequestHeader("Authorization") String token) {
        try {
            System.out.println("Token recibido: " + token);
            String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
            System.out.println("Username extraído del token: " + username);
            User user = userService.findByUsername(username);
            List<Post> posts = postService.getPostsFromFollowing(user.getId());
            System.out.println("Total posts: " + posts.size());
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o usuario no encontrado");
        }
    }


    @PostMapping("/create")
    public ResponseEntity<?> createPost(
        @RequestHeader("Authorization") String token,
        @RequestBody Map<String, String> request
    ) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userService.findByUsername(username);

        String content = request.get("content");
        String imageUrl = request.get("imageUrl"); // Debes enviar la URL o null

        Post newPost = postService.createPost(user.getId(), content, imageUrl);
        return ResponseEntity.ok(newPost);
    }
}

