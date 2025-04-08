package com.example.rolas.Repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rolas.model.AlbumArtists;
import com.example.rolas.model.Albums;
import com.example.rolas.model.Artista;

@Repository
public interface AlbumArtistsRepositorio extends JpaRepository<AlbumArtists, Integer> {
	 List<AlbumArtists> findByArtistaId(Integer artistaId);
	 Optional<AlbumArtists> findByAlbum(Albums album);
	 
}

