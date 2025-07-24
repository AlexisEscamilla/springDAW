package com.example.rolas.DTO;

import java.util.List;
import java.util.stream.Collectors;

import com.example.rolas.model.Artista;

public class ArtistDTO {
    private Integer id;
    private String bio;
    private boolean verificado;
    private String discografica;
    private List<SongDTO> canciones;

    public ArtistDTO(Artista artista) {
        this.id = artista.getId();
        this.bio = artista.getBio();
        this.verificado = artista.isVerificado();
        this.discografica = artista.getDiscografica();
        this.canciones = artista.getSongArtists().stream()
            .map(sa -> new SongDTO(sa.getSong()))
            .collect(Collectors.toList());
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public List<SongDTO> getCanciones() {
		return canciones;
	}

	public void setCanciones(List<SongDTO> canciones) {
		this.canciones = canciones;
	}
    
    
}


