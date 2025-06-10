package com.example.rolas.service;

import com.example.rolas.model.Post;
import com.example.rolas.model.PostLikes;
import com.example.rolas.model.User;
import com.example.rolas.Repositorio.PostLikesRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostLikesService {

    @Autowired
    private PostLikesRepositorio postLikesRepository;

    public boolean toggleLike(User user, Post post) {
        if (postLikesRepository.existsByUserIdAndPostId(user.getId(), post.getId())) {
            postLikesRepository.deleteByUserIdAndPostId(user.getId(), post.getId());
            return false; // like eliminado
        } else {
            PostLikes like = new PostLikes();
            like.setUser(user);
            like.setPost(post);
            postLikesRepository.save(like);
            return true; // like agregado
        }
    }
}