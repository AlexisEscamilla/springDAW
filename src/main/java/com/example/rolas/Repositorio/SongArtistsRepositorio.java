package com.example.rolas.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rolas.model.SongArtists;
import com.example.rolas.model.Songs;

@Repository
public interface SongArtistsRepositorio extends JpaRepository<SongArtists, Integer> {
	List<SongArtists> findBySong(Songs song);
}

