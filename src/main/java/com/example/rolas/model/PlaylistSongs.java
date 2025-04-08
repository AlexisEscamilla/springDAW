package com.example.rolas.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "playlist_songs") 
public class PlaylistSongs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "playlist_id", referencedColumnName = "id", nullable = false)
    private Playlists playlist;

    @ManyToOne
    @JoinColumn(name = "song_id", referencedColumnName = "id", nullable = false)
    private Songs song;

    @Column(name = "posicion")
    private String posicion;

    @Column(name = "added_at")
    private LocalDate addedAt;

    // Getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Playlists getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlists playlist) {
        this.playlist = playlist;
    }

    public Songs getSong() {
        return song;
    }

    public void setSong(Songs song) {
        this.song = song;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public LocalDate getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDate addedAt) {
        this.addedAt = addedAt;
    }
}
