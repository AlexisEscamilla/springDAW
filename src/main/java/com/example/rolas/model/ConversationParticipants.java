package com.example.rolas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "conversation_participants")
public class ConversationParticipants {

    @EmbeddedId
    private ConversationParticipantsId id;

    @ManyToOne
    @MapsId("conversationId") 
    @JoinColumn(name = "conversation_id")
    @JsonBackReference
    @JsonIgnore
    private Conversations conversation;

    @ManyToOne
    @MapsId("userId") 
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    // Getters y setters
    public ConversationParticipantsId getId() {
        return id;
    }

    public void setId(ConversationParticipantsId id) {
        this.id = id;
    }

    public Conversations getConversation() {
        return conversation;
    }

    public void setConversation(Conversations conversation) {
        this.conversation = conversation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}