package com.example.rolas.DTO;

public class ConversationDTO {
    private Integer id;
    private String groupName;

    // Constructor
    public ConversationDTO(Integer id, String groupName) {
        this.id = id;
        this.groupName = groupName;
    }

    // Getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}