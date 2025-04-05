package com.example.rolas.Repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.rolas.model.Albums;
import com.example.rolas.model.Songs;

public interface  SongsRepositorio extends JpaRepository<Songs, Integer>{
	 Optional<Songs> findByTitle(String title);
	 @Query("SELECT s FROM Songs s JOIN s.songArtists sa WHERE sa.artista.id = :artistaId")
	 List<Songs> findByArtistaId(@Param("artistaId") Integer artistaId);
}
