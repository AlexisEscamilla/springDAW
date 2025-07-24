package com.example.rolas.DTO;

import com.example.rolas.model.Songs;

public class SongDTO {
    private Integer id;
    private String title;
    private String duration;
    private String gender;
    private String fileUrl;
    private String coverUrl;
    private AlbumDTO album;

    public SongDTO(Songs song) {
        this.id = song.getId();
        this.title = song.getTitle();
        this.duration = song.getDuration();
        this.gender = song.getGender();
        this.fileUrl = song.getFileUrl();
        this.coverUrl = song.getCoverUrl();

        if (song.getAlbum() != null) {
            this.album = new AlbumDTO(song.getAlbum());
        }
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

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public AlbumDTO getAlbum() {
		return album;
	}

	public void setAlbum(AlbumDTO album) {
		this.album = album;
	}
    
    
}
