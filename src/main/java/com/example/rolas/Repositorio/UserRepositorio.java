package com.example.rolas.Repositorio;

import com.example.rolas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepositorio extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByNombre(String nombre);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    List<User> findByNombreContainingIgnoreCase(String nombre);
    List<User> findByNombreContainingIgnoreCaseOrUsernameContainingIgnoreCase(
    	    String nombre, 
    	    String username
    	);
    
    @Query(value = """
    	    SELECT u.*, 
    	      (
    	        SELECT COUNT(*) FROM followers f1 
    	        WHERE f1.following_id = u.id 
    	          AND f1.follower_id IN (
    	            SELECT f2.following_id FROM followers f2 WHERE f2.follower_id = :userId
    	          )
    	      ) AS seguidoresEnComun,
    	      (
    	        SELECT COUNT(*) FROM followers f3 
    	        WHERE f3.follower_id = u.id 
    	          AND f3.following_id IN (
    	            SELECT f4.following_id FROM followers f4 WHERE f4.follower_id = :userId
    	          )
    	      ) AS siguiendoEnComun
    	    FROM users u
    	    WHERE u.id != :userId
    	      AND u.id NOT IN (
    	        SELECT f.following_id FROM followers f WHERE f.follower_id = :userId
    	      )
    	    ORDER BY seguidoresEnComun DESC, siguiendoEnComun DESC
    	    LIMIT :limit
    	    """, nativeQuery = true)
    	List<User> findSuggestedUsers(@Param("userId") Integer userId, @Param("limit") int limit);
}