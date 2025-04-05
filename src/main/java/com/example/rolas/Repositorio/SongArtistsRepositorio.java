package com.example.rolas.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rolas.model.SongArtists;

@Repository
public interface SongArtistsRepositorio extends JpaRepository<SongArtists, Integer> {
}

