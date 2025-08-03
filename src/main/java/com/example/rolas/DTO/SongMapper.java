package com.example.rolas.DTO;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.rolas.model.Songs;

@Component
public class SongMapper {

    public static SongDTO toDto(Songs song) {
        return new SongDTO(song);
    }

    public static List<SongDTO> toDtoList(List<Songs> songs) {
        return songs.stream()
                .map(SongMapper::toDto)
                .collect(Collectors.toList());
    }
}
