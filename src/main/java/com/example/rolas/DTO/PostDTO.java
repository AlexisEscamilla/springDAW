package com.example.rolas.DTO;

import java.time.LocalDateTime;
import java.util.List;



public class PostDTO {
    private Integer id;
    private String username;
    private String profilePicture;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
    private int likeCount;
    private int commentCount;
    private boolean likedByCurrentUser;
    private List<PostCommentsDTO> comments;
    // Getters y setters
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
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public boolean isLikedByCurrentUser() {
		return likedByCurrentUser;
	}
	public void setLikedByCurrentUser(boolean likedByCurrentUser) {
		this.likedByCurrentUser = likedByCurrentUser;
	}
	public List<PostCommentsDTO> getComments() {
		return comments;
	}
	public void setComments(List<PostCommentsDTO> comments) {
		this.comments = comments;
	}
    
    
    
}


