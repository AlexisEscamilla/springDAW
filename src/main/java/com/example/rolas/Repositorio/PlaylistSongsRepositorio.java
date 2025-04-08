package com.example.rolas.Repositorio;

import com.example.rolas.model.AlbumArtists;
import com.example.rolas.model.PlaylistSongs;
import com.example.rolas.model.Playlists;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistSongsRepositorio extends JpaRepository<PlaylistSongs, Integer> {
    List<PlaylistSongs> findByPlaylist(Playlists playlist);
}
