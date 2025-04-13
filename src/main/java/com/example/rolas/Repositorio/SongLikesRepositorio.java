package com.example.rolas.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.rolas.model.SongLikes;
import com.example.rolas.model.Songs;
import com.example.rolas.model.User;

public interface SongLikesRepositorio extends JpaRepository<SongLikes, Integer> {
    boolean existsByUserAndSong(User user, Songs song);
    SongLikes findByUserAndSong(User user, Songs song);
    int countBySong(Songs song);
}