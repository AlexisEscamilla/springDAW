package com.example.rolas.DTO;

public class ChatDetailsDTO {
    private Integer id;
    private boolean isGroup;
    private String groupName;
    private String groupPhoto;
    private String partnerName;
    private String partnerPhoto;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public boolean isGroup() {
		return isGroup;
	}
	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupPhoto() {
		return groupPhoto;
	}
	public void setGroupPhoto(String groupPhoto) {
		this.groupPhoto = groupPhoto;
	}
	public String getPartnerName() {
		return partnerName;
	}
	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
	public String getPartnerPhoto() {
		return partnerPhoto;
	}
	public void setPartnerPhoto(String partnerPhoto) {
		this.partnerPhoto = partnerPhoto;
	}

    // Getters y setters
    
    
}
