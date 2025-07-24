package com.example.rolas.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.rolas.model.Post;
import com.example.rolas.model.PostComments;

public class PostCommentsDTO {
    private Integer id;
    private String username;
    private String profilePicture;
    private String content;
    private String imagenAdjuntada;
    private LocalDateTime createdAt;

    public PostCommentsDTO(PostComments comment) {
        this.id = comment.getId();
        this.username = comment.getUser().getUsername();
        this.profilePicture = comment.getUser().getProfilePicture(); // asegúrate de tener este campo
        this.content = comment.getCommentText();
        this.imagenAdjuntada = comment.getImagenAdjuntada();
        this.createdAt = comment.getCreatedAt();
    }
    
    

	public PostCommentsDTO() {
		// TODO Auto-generated constructor stub
	}



	public String getImagenAdjuntada() {
		return imagenAdjuntada;
	}



	public void setImagenAdjuntada(String imagenAdjuntada) {
		this.imagenAdjuntada = imagenAdjuntada;
	}



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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
    

    // getters y setters
}


