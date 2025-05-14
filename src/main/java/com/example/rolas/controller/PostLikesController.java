package com.example.rolas.controller;

import com.example.rolas.model.Post;
import com.example.rolas.model.PostLikes;
import com.example.rolas.model.User;

import jakarta.servlet.http.HttpServletRequest;

import com.example.rolas.Repositorio.PostLikesRepositorio;
import com.example.rolas.Repositorio.PostRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
public class PostLikesController {

    @Autowired
    private PostLikesRepositorio postLikesRepository;

    @Autowired
    private PostRepositorio postRepository;

    @Autowired
    private UserRepositorio userRepository;

    @PostMapping("/{postId}/likes")
    public String toggleLike(@PathVariable Integer postId, 
                             @AuthenticationPrincipal UserDetails userDetails,
                             HttpServletRequest request) {
        User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post no encontrado"));

        if (postLikesRepository.existsByUserAndPost(user, post)) {
            PostLikes like = postLikesRepository.findByUserAndPost(user, post);
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postLikesRepository.delete(like);
        } else {
            PostLikes like = new PostLikes();
            like.setUser(user);
            like.setPost(post);
            post.setLikeCount(post.getLikeCount() + 1);
            postLikesRepository.save(like);
        }

        postRepository.save(post);

        // Redirige a la página desde donde vino el usuario
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/feed");
    }

}
