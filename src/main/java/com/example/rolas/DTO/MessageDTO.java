package com.example.rolas.DTO;

import java.time.LocalDateTime;

public class MessageDTO {
	 private String texto;
	 private String senderName;
	 private String senderPhoto;
	 private boolean isMine;
	 private LocalDateTime createdAt;
	 private int conversationId;
	 
	 
	 
	public int getConversationId() {
		return conversationId;
	}

	public void setConversationId(int conversationId) {
		this.conversationId = conversationId;
	}

	public MessageDTO(String texto, String senderName, String senderPhoto, boolean isMine, LocalDateTime createdAt) {
		super();
		this.texto = texto;
		this.senderName = senderName;
		this.senderPhoto = senderPhoto;
		this.isMine = isMine;
		this.createdAt = createdAt;
	}
	
	public MessageDTO() {
		super();
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getSenderPhoto() {
		return senderPhoto;
	}
	public void setSenderPhoto(String senderPhoto) {
		this.senderPhoto = senderPhoto;
	}
	public boolean isMine() {
		return isMine;
	}
	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
 
	 
}
