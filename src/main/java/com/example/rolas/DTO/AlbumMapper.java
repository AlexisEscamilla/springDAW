package com.example.rolas.DTO;

import java.util.List;
import java.util.stream.Collectors;

import com.example.rolas.model.Albums;

public class AlbumMapper {
	
	public static AlbumDTO toDto(Albums album) {
        return new AlbumDTO(album);
    }

    public static List<AlbumDTO> toDtoList(List<Albums> Albums) {
        return Albums.stream()
                .map(AlbumMapper::toDto)
                .collect(Collectors.toList());
    }
}
