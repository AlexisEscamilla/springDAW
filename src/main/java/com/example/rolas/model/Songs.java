package com.example.rolas.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;


@Entity
public class Songs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "duration")
    private String duration;

    @Column(name = "gender")
    private String gender; 

    @Column(name = "file_url")
    private String fileUrl; 

    @Column(name = "cover_url")
    private String coverUrl; 
    
    @Column(name = "like_count")
    private int likeCount; 

    public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	@ManyToOne
    @JoinColumn(name = "album_id")
    private Albums album;  

    
    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL)
    private Set<SongArtists> songArtists;  

    @Column(name = "created_at")
    private LocalDate createdAt; 

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public Albums getAlbum() {
		return album;
	}

	public void setAlbum(Albums album) {
		this.album = album;
	}

	public Set<SongArtists> getSongArtists() {
		return songArtists;
	}

	public void setSongArtists(Set<SongArtists> songArtists) {
		this.songArtists = songArtists;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

    
}
