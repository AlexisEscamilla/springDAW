package com.example.rolas.Repositorio;

import com.example.rolas.model.Post;
import com.example.rolas.model.PostLikes;
import com.example.rolas.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikesRepositorio extends JpaRepository<PostLikes, Integer> {
    boolean existsByUserIdAndPostId(Integer userId, Integer postId);
    void deleteByUserIdAndPostId(Integer userId, Integer postId);
    boolean existsByUserAndPost(User user, Post post);
 // Obtener el registro de like de un usuario en un post
    PostLikes findByUserAndPost(User user, Post post);
}