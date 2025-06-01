package com.example.rolas.DTO;

import java.time.LocalDate;
import java.util.List;

import com.example.rolas.model.Playlists;

public class PlaylistsDTO {
    
    private Integer id;
    private Integer userId;
    private String title;
    private String description;
    private String coverImage;
    private LocalDate createdAt;
    private List<Integer> songIds;

    // Constructors
    public PlaylistsDTO() {}

    public PlaylistsDTO(Integer id, Integer userId, String title, String description, String coverImage, LocalDate createdAt, List<Integer> songIds) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.coverImage = coverImage;
        this.createdAt = createdAt;
        this.songIds = songIds;
    }
    
    public PlaylistsDTO(Playlists playlist) {
        this.id = playlist.getId();
        this.userId = playlist.getUser().getId();
        this.title = playlist.getTitle();
        this.description = playlist.getDescription();
        this.coverImage = playlist.getCoverImage();
        this.createdAt = playlist.getCreatedAt();
        this.songIds = playlist.getPlaylistSongs().stream()
                        .map(ps -> ps.getSong().getId()) // Asume que PlaylistSongs tiene getSong().getId()
                        .toList();
    }


    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public List<Integer> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<Integer> songIds) {
        this.songIds = songIds;
    }
}

