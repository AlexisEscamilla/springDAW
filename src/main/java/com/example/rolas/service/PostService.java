package com.example.rolas.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.rolas.Repositorio.FollowersRepositorio;
import com.example.rolas.Repositorio.PostLikesRepositorio;
import com.example.rolas.Repositorio.PostRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.model.Post;
import com.example.rolas.model.PostLikes;
import com.example.rolas.model.User;

@Service
public class PostService {

    @Autowired
    private PostRepositorio postRepository;
    
    @Autowired
    private PostLikesRepositorio postlikesRepository;
    
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

        // crea el post
        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        post.setImageUrl(imageUrl);
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);

        // incrementa el numero de posts del usuario
        user.setNumPosts(user.getNumPosts() + 1);
        userRepository.save(user);

        return post;
    }
    
    
    public void editPost(Integer postId, Integer userId, String content, String imageUrl, boolean removeImage) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post no encontrado"));
        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para editar este post");
        }
        post.setContent(content);
        if (removeImage) {
            post.setImageUrl(null);
        } else if (imageUrl != null) {
            post.setImageUrl(imageUrl);
        }
        postRepository.save(post);
    }
    
    
    public void deletePost(Integer postId, String username) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post no encontrado"));
        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("No tienes permiso para eliminar este post");
        }
        postRepository.delete(post);
    }

    public boolean toggleLike(int postId, int userId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (postOpt.isEmpty() || userOpt.isEmpty()) {
            return false;
        }

        Post post = postOpt.get();
        User user = userOpt.get();
        
        // Verificar si ya existe un like para este usuario y post
        if (postlikesRepository.existsByUserIdAndPostId(userId, postId)) {
            // Si ya existe el like, lo eliminamos
            postlikesRepository.deleteByUserIdAndPostId(userId, postId);
            return false; // Ya no le gusta
        } else {
            // Si no existe, creamos un nuevo like
            PostLikes newLike = new PostLikes();
            newLike.setUser(user);
            newLike.setPost(post);
            postlikesRepository.save(newLike);
            return true; // Ahora le gusta
        }
    }
    
}