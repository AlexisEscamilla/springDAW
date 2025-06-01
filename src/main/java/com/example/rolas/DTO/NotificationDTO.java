package com.example.rolas.DTO;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private String type;
    private LocalDateTime createdAt;
    private Integer contentId;
    private SourceUserDTO sourceUser;

    // getters y setters

    public static class SourceUserDTO {
        private Integer id;
        private String username;
        // getters y setters
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
        
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getContentId() {
		return contentId;
	}

	public void setContentId(Integer contentId) {
		this.contentId = contentId;
	}

	public SourceUserDTO getSourceUser() {
		return sourceUser;
	}

	public void setSourceUser(SourceUserDTO sourceUser) {
		this.sourceUser = sourceUser;
	}
    
    
}
