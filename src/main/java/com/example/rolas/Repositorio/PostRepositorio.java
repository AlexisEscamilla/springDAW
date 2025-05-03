package com.example.rolas.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.rolas.model.Post;

@Repository
public interface PostRepositorio extends JpaRepository<Post, Integer> {
    // Obtener posts de los usuarios que sigue un usuario específico
	int countByUserId(Integer userId);
    List<Post> findByUserIdInOrderByCreatedAtDesc(List<Integer> userIds);
}
