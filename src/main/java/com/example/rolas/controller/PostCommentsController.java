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

import java.io.IOException;
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
        // Obtener el usuario autenticado
        User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener el post
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post no encontrado"));

        // Crear el comentario
        PostComments comment = new PostComments();
        comment.setUser(user);
        comment.setPost(post);
        comment.setCommentText(commentText);

        // Manejar la imagen si se proporciona
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String imagePath = "/uploads/" + imageUrl.getOriginalFilename();
            // Aquí puedes guardar la imagen en el servidor si es necesario
            comment.setImagenAdjuntada(imagePath);
        }

        // Incrementar el contador de comentarios del post
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        postCommentsRepository.save(comment);

        // Redirigir a la página desde donde vino el usuario
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/feed");
    }


    @GetMapping("/{postId}/comments")
    public List<PostComments> getComments(@PathVariable Integer postId) {
        return postCommentsRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }
}
