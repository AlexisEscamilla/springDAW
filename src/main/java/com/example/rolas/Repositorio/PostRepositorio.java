package com.example.rolas.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rolas.model.Post;

@Repository
public interface PostRepositorio extends JpaRepository<Post, Integer> {
	int countByUserId(Integer userId);
    List<Post> findByUserIdInOrderByCreatedAtDesc(List<Integer> userIds);
    List<Post> findByUserIdOrderByCreatedAtDesc(Integer userId);
}
