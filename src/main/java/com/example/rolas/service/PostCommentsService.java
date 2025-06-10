package com.example.rolas.service;

import com.example.rolas.model.Post;
import com.example.rolas.model.PostComments;
import com.example.rolas.model.User;
import com.example.rolas.Repositorio.PostCommentsRepositorio;
import com.example.rolas.Repositorio.PostRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostCommentsService {

    @Autowired
    private PostCommentsRepositorio postCommentsRepository;

    @Autowired
    private PostRepositorio postRepository;

    public PostComments addComment(User user, Post post, String commentText, String imageUrl) {
        PostComments comment = new PostComments();
        comment.setUser(user);
        comment.setPost(post);
        comment.setCommentText(commentText);
        comment.setImagenAdjuntada(imageUrl);

        // incrementa el contador de comentarios
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return postCommentsRepository.save(comment);
    }

    public List<PostComments> getCommentsByPost(Post post) {
        return postCommentsRepository.findByPostIdOrderByCreatedAtAsc(post.getId());
    }
    
    public List<PostComments> getAllCommentsForPosts(List<Post> posts) {
        List<Integer> postIds = posts.stream().map(Post::getId).toList();
        return postCommentsRepository.findByPostIdInOrderByCreatedAtAsc(postIds);
    }
}