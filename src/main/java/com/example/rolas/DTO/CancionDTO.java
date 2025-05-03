package com.example.rolas.DTO;

import com.example.rolas.model.Songs;

public class CancionDTO {
    private Integer id;
    private String title;
    private String duration;
    private String fileUrl;
    private String coverUrl;
    private String gender;
    private int likeCount;

    public CancionDTO(Songs song) {
        this.id = song.getId();
        this.title = song.getTitle();
        this.duration = song.getDuration();
        this.fileUrl = song.getFileUrl();
        this.coverUrl = song.getCoverUrl();
        this.gender = song.getGender();
        this.likeCount = song.getLikeCount();
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

    // Getters y setters opcionales
    
}

