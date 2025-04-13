package com.example.rolas.Repositorio;

import com.example.rolas.model.Albums;
import com.example.rolas.model.Artista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlbumsRepositorio extends JpaRepository<Albums, Integer> {

    List<Albums> findByAlbumArtists_Artista(Artista artista);
    List<Albums> findByTitleContainingIgnoreCase(String title);
}

