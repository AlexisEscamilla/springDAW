package com.example.rolas.Repositorio;

import com.example.rolas.model.Playlists;
import com.example.rolas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistsRepositorio extends JpaRepository<Playlists, Integer> {
    List<Playlists> findByUser(User user);
    List<Playlists> findByTitleContainingIgnoreCase(String title);
}
