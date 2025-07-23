package com.example.rolas.controller;

import com.example.rolas.model.Post;
import com.example.rolas.model.User;
import com.example.rolas.service.PostService;
import com.example.rolas.service.UserService;
import com.example.rolas.DTO.PostDTO;
import com.example.rolas.DTO.PostMapper;
import com.example.rolas.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PostsRestController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    
    @Autowired
    private PostMapper postMapper;

    

    // GET /api/posts - Obtener feed
    @GetMapping("/posts")
    public ResponseEntity<?> getFeed(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        User currentUser = userService.findByUsername(username);
        List<Post> posts = postService.getPostsFromFollowing(currentUser.getId());

        List<PostDTO> postDtos = posts.stream()
            .map(post -> PostMapper.toDto(post, currentUser))
            .collect(Collectors.toList());

        return ResponseEntity.ok(postDtos);
    }



    // POST /api/posts - Crear nuevo post
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> request
    ) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userService.findByUsername(username);
        String content = request.get("content");
        String imageUrl = request.get("imageUrl");
        Post newPost = postService.createPost(user.getId(), content, imageUrl);
        return ResponseEntity.ok(newPost);
    }

    // POST /api/posts/{id}/like - Like/unlike post
    @PostMapping("/posts/{id}/like")
    public ResponseEntity<?> likePost(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") int postId
    ) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userService.findByUsername(username);
        boolean liked = postService.toggleLike(postId, user.getId());
        return ResponseEntity.ok(Map.of("liked", liked));
    }
}
