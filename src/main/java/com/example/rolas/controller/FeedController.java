package com.example.rolas.controller;

import com.example.rolas.Repositorio.PostLikesRepositorio;
import com.example.rolas.model.Post;
import com.example.rolas.model.PostComments;
import com.example.rolas.model.User;
import com.example.rolas.service.PostCommentsService;
import com.example.rolas.service.PostService;
import com.example.rolas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private PostCommentsService postCommentsService;
    
    @Autowired
    private PostLikesRepositorio postLikesRepository;

    @GetMapping
    public String getFeed(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Post> posts = postService.getPostsFromFollowing(user.getId()); 
        List<PostComments> allComments = postCommentsService.getAllCommentsForPosts(posts);
        
        for (Post post : posts) {
            boolean liked = postLikesRepository.existsByUserAndPost(user, post);
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
        
        Map<Integer, Integer> postCommentCounts = new HashMap<>();
        for (Post post : posts) {
            int count = (int) allComments.stream().filter(c -> c.getPost().getId().equals(post.getId())).count();
            postCommentCounts.put(post.getId(), count);
        }
        model.addAttribute("postCommentCounts", postCommentCounts);
        model.addAttribute("usuario", user);
        model.addAttribute("posts", postService.getPostsFromFollowing(user.getId()));
        model.addAttribute("comments", allComments);

        return "feed";
    }

    @PostMapping("/create")
    public String createPost(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String content,
                             @RequestParam(value = "imageUrl", required = false) MultipartFile imageFile) {
        User user = userService.findByUsername(userDetails.getUsername());
        String imageUrl = null;

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String uploadsDir = "uploads/";
                Files.createDirectories(Paths.get(uploadsDir));
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(uploadsDir, fileName);
                imageFile.transferTo(filePath);
                imageUrl = fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        postService.createPost(user.getId(), content, imageUrl);

        return "redirect:/feed";
    }
}