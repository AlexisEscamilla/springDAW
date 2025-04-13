package com.example.rolas.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.rolas.model.SongComments;
import com.example.rolas.model.Songs;

import java.util.List;

public interface SongCommentsRepositorio extends JpaRepository<SongComments, Integer> {
    List<SongComments> findBySong(Songs song);
}