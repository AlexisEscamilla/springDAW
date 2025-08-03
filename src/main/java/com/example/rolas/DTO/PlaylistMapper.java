package com.example.rolas.DTO;

import java.util.List;
import java.util.stream.Collectors;

import com.example.rolas.model.Playlists;

public class PlaylistMapper {
	
	public static PlaylistsDTO toDto(Playlists playlist) {
        return new PlaylistsDTO(playlist);
    }

    public static List<PlaylistsDTO> toDtoList(List<Playlists> playlist) {
        return playlist.stream()
                .map(PlaylistMapper::toDto)
                .collect(Collectors.toList());
    }
}
