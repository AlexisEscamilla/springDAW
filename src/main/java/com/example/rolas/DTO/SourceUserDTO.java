package com.example.rolas.DTO;

public class SourceUserDTO {
    private Integer id;
    private String username;

    public SourceUserDTO(Integer id, String username) {
        this.id = id;
        this.username = username;
    }

    public SourceUserDTO() {
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

    // Getters y setters
    
    	
}

