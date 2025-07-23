package com.example.rolas.model;

import jakarta.persistence.*;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "artistas")
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String bio;
    private boolean verificado;
    private String discografica;
    

    
    @OneToMany(mappedBy = "artista", cascade = CascadeType.ALL)
    private Set<SongArtists> songArtists;

   
    @OneToMany(mappedBy = "artista", cascade = CascadeType.ALL)
    private Set<AlbumArtists> albumArtists;

    // Getters y setters

    public Set<SongArtists> getSongArtists() {
        return songArtists;
    }

    public void setSongArtists(Set<SongArtists> songArtists) {
        this.songArtists = songArtists;
    }

    public Set<AlbumArtists> getAlbumArtists() {
        return albumArtists;
    }

    public void setAlbumArtists(Set<AlbumArtists> albumArtists) {
        this.albumArtists = albumArtists;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public String getDiscografica() {
        return discografica;
    }

    public void setDiscografica(String discografica) {
        this.discografica = discografica;
    }
}


