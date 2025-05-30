package com.example.rolas.controller;

import com.example.rolas.model.Post;
import com.example.rolas.model.PostComments;
import com.example.rolas.model.User;

import jakarta.servlet.http.HttpServletRequest;

import com.example.rolas.Repositorio.PostCommentsRepositorio;
import com.example.rolas.Repositorio.PostRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostCommentsController {

    @Autowired
    private PostCommentsRepositorio postCommentsRepository;

    @Autowired
    private PostRepositorio postRepository;

    @Autowired
    private UserRepositorio userRepository;

    @PostMapping("/{postId}/comments")
    public String addComment(@PathVariable Integer postId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String commentText,
                             @RequestParam(required = false) MultipartFile imageUrl,
                             HttpServletRequest request) throws IOException {
        User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post no encontrado"));

        PostComments comment = new PostComments();
        comment.setUser(user);
        comment.setPost(post);
        comment.setCommentText(commentText);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            String uploadsDir = System.getProperty("user.dir") + File.separator + "uploads";
            File uploadDir = new File(uploadsDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String fileName = System.currentTimeMillis() + "_" + imageUrl.getOriginalFilename();
            Path filePath = Paths.get(uploadsDir, fileName);
            imageUrl.transferTo(filePath.toFile());
            comment.setImagenAdjuntada(fileName);
        }

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        postCommentsRepository.save(comment);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/feed");
    }


    @GetMapping("/{postId}/comments")
    public List<PostComments> getComments(@PathVariable Integer postId) {
        return postCommentsRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }
}
