package com.example.rolas.DTO;

import java.time.LocalDate;

import com.example.rolas.model.Albums;

public class AlbumDTO {
    private Integer id;
    private String title;
    private String coverArt;
    private LocalDate releaseDate;

    public AlbumDTO(Albums album) {
        this.id = album.getId();
        this.title = album.getTitle();
        this.coverArt = album.getCoverArt();
        this.releaseDate = album.getReleaseDate();
    }

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

    // Getters y setters opcionales
    
    
}

