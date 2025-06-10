package com.example.rolas.Repositorio;

import com.example.rolas.model.PlaylistSongs;
import com.example.rolas.model.Playlists;

import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistSongsRepositorio extends JpaRepository<PlaylistSongs, Integer> {
	 @Transactional
	 @Modifying
	 @Query("DELETE FROM PlaylistSongs ps WHERE ps.playlist.id = :playlistId AND ps.song.id = :songId")
	 void deleteByPlaylistIdAndSongId(Integer playlistId, Integer songId);
	List<PlaylistSongs> findByPlaylist(Playlists playlist);
	@Transactional
	@Modifying
	@Query("DELETE FROM PlaylistSongs ps WHERE ps.song.id = :songId")
	void deleteBySongId(Integer songId);
}
