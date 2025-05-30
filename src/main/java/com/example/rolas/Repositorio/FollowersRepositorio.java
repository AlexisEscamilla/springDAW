package com.example.rolas.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.rolas.model.Followers;
import com.example.rolas.model.User;

import java.util.List;

public interface FollowersRepositorio extends JpaRepository<Followers, Integer> {
    boolean existsByFollowerAndFollowing(User follower, User following);
    List<Followers> findByFollower(User follower);
    List<Followers> findByFollowerId(Integer followerId);
    List<Followers> findByFollowing(User following);
    void deleteByFollowerAndFollowing(User follower, User following);
    int countByFollower(User follower);
    int countByFollowing(User following);
    
}