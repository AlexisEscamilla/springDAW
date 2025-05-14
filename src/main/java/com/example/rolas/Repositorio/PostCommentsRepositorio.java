package com.example.rolas.Repositorio;

import com.example.rolas.model.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentsRepositorio extends JpaRepository<PostComments, Integer> {
    List<PostComments> findByPostIdOrderByCreatedAtAsc(Integer postId);
    List<PostComments> findByPostIdInOrderByCreatedAtAsc(List<Integer> postIds);
}