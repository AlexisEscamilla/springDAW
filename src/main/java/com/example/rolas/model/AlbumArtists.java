package com.example.rolas.model;

import jakarta.persistence.*;

@Entity
public class AlbumArtists {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "album_id", referencedColumnName = "id", nullable = false)
    private Albums album;

    @ManyToOne
    @JoinColumn(name = "artist_id", referencedColumnName = "id", nullable = false)
    private Artista artista;

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Albums getAlbum() {
        return album;
    }

    public void setAlbum(Albums album) {
        this.album = album;
    }

    public Artista getArtista() {
        return artista;
    }
    
    public User getUsuario() {
        return artista.getUser();
    }

    public void setArtista(Artista artista) {
        this.artista = artista;
    }
}

