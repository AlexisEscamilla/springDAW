package com.example.rolas.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 50)
    private String nombre;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "profile_picture")
    private String profile_picture;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "seguidores", nullable = false)
    private int seguidores = 0;
    
    @Column(name = "siguiendo", nullable = false)
    private int siguiendo = 0;

    public int getSeguidores() {
		return seguidores;
	}
    
    @Column(name = "num_posts", nullable = false)
    private Integer numPosts;
    
    

	public String getProfile_picture() {
		return profile_picture;
	}

	public void setProfile_picture(String profile_picture) {
		this.profile_picture = profile_picture;
	}


	public Integer getNumPosts() {
		return numPosts;
	}

	public void setNumPosts(Integer numPosts) {
		this.numPosts = numPosts;
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

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Artista artista;  // Relación con Artista si es artista

    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String nombre, String username, String email, String passwordHash) {
        this.nombre = nombre;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
    }

    public Artista getArtista() {
        return artista;
    }

    public void setArtista(Artista artista) {
        this.artista = artista;
    }
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    // ✅ Getter/Setter corregidos para que Thymeleaf los reconozca
    public String getProfilePicture() {
        return profile_picture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profile_picture = profilePicture;
    }

    public String getUserType() {
        return this.artista != null ? "Artista" : "Usuario";
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Métodos de Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> "ROLE_USER");
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}

