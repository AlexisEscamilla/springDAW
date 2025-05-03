package com.example.rolas.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.rolas.Repositorio.FollowersRepositorio;
import com.example.rolas.Repositorio.PostRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.model.Post;
import com.example.rolas.model.User;

@Service
public class PostService {

    @Autowired
    private PostRepositorio postRepository;
    
    @Autowired
    private UserRepositorio userRepository;

    @Autowired
    private FollowersRepositorio followersRepository;

    public List<Post> getPostsFromFollowing(Integer userId) {
        List<Integer> followingIds = followersRepository.findByFollowerId(userId)
            .stream()
            .map(follower -> follower.getFollowing().getId())
            .collect(Collectors.toList());

        return postRepository.findByUserIdInOrderByCreatedAtDesc(followingIds);
    }

    public Post createPost(Integer userId, String content, String imageUrl) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear el post
        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        post.setImageUrl(imageUrl);
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);

        // Incrementar el número de posts del usuario
        user.setNumPosts(user.getNumPosts() + 1);
        userRepository.save(user);

        return post;
    }
}