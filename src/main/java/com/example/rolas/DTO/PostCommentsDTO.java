package com.example.rolas.DTO;

import java.time.LocalDateTime;

public class PostCommentsDTO {
    private Integer id;
    private String username;
    private String profilePicture;
    private String commentText;
    private String imagenAdjuntada;
    private LocalDateTime createdAt;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	public String getCommentText() {
		return commentText;
	}
	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
	public String getImagenAdjuntada() {
		return imagenAdjuntada;
	}
	public void setImagenAdjuntada(String imagenAdjuntada) {
		this.imagenAdjuntada = imagenAdjuntada;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
    
}

