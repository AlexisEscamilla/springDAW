package com.example.rolas.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "albums")
public class Albums {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "cover_art")
    private String coverArt; 
    
    @Column(name = "release_date")
    private LocalDate releaseDate;

   
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    private Set<AlbumArtists> albumArtists = new HashSet<>();  

    // Getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverArt() {
        return coverArt;
    }

    public void setCoverArt(String coverArt) {
        this.coverArt = coverArt;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Set<AlbumArtists> getAlbumArtists() {
        return albumArtists;
    }

    public void setAlbumArtists(Set<AlbumArtists> albumArtists) {
        this.albumArtists = albumArtists;
    }
}

