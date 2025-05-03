package com.example.rolas.DTO;

import com.example.rolas.model.User;

public class UserDTO {
	private Integer id;
    private String nombre;
    private String username;
    private String profilePicture;
    private int seguidores;
    private int siguiendo;

    public UserDTO(User user) {
        this.id = user.getId();
        this.nombre = user.getNombre();
        this.username = user.getUsername();
        this.profilePicture = user.getProfilePicture();
        this.seguidores = user.getSeguidores();
        this.siguiendo = user.getSiguiendo();
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
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

	public int getSeguidores() {
		return seguidores;
	}

	public void setSeguidores(int seguidores) {
		this.seguidores = seguidores;
	}

	public int getSiguiendo() {
		return siguiendo;
	}

	public void setSiguiendo(int siguiendo) {
		this.siguiendo = siguiendo;
	}
    
    
}
