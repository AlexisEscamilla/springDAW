package com.example.rolas.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.rolas.model.Post;
import com.example.rolas.model.User;
import com.example.rolas.service.PostService;

@Controller
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping
    public String getFeed(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User usuario = (User) userDetails; // Asegúrate de que `Usuario` implemente `UserDetails`
        List<Post> posts = postService.getPostsFromFollowing(usuario.getId());
        model.addAttribute("posts", posts);
        return "feed";
    }

    
    @PostMapping
    public String createPost(@RequestParam String contenido,
                             @RequestParam(value = "imageUrl", required = false) MultipartFile imageFile,
                             @AuthenticationPrincipal UserDetails userDetails) {
        User usuario = (User) userDetails;
        String imageUrl = null;

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String uploadsDir = "uploads/";
                String realPathToUploads = new java.io.File(uploadsDir).getAbsolutePath();
                Files.createDirectories(Paths.get(realPathToUploads));
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(realPathToUploads, fileName);
                imageFile.transferTo(filePath);
                imageUrl = fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        postService.createPost(usuario.getId(), contenido, imageUrl);
        return "redirect:/posts";
    }
    
}
