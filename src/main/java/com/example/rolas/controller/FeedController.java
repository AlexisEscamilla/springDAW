package com.example.rolas.controller;

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
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private PostCommentsService postCommentsService;

    @GetMapping
    public String getFeed(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Post> posts = postService.getPostsFromFollowing(user.getId()); 
        List<PostComments> allComments = postCommentsService.getAllCommentsForPosts(posts);
        
        System.out.println("Comentarios:");
        allComments.forEach(comment -> {
            if (comment != null) {
                System.out.println("Comentario ID: " + comment.getId() + ", Texto: " + comment.getCommentText());
            } else {
                System.out.println("Comentario nulo encontrado");
            }
        });
        model.addAttribute("usuario", user);
        model.addAttribute("posts", postService.getPostsFromFollowing(user.getId()));
        model.addAttribute("comments", allComments);

        return "feed";
    }

    @PostMapping("/create")
    public String createPost(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String content,
                             @RequestParam(required = false) String imageUrl) {
        User user = userService.findByUsername(userDetails.getUsername());
        postService.createPost(user.getId(), content, imageUrl);

        return "redirect:/feed";
    }
}