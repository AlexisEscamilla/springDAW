package com.example.rolas.DTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.rolas.model.Post;
import com.example.rolas.model.PostComments;
import com.example.rolas.model.User;

@Component
public class PostMapper {

    public static PostDTO toDto(Post post, User currentUser) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setUsername(post.getUser().getUsername());
        dto.setProfilePicture(post.getUser().getProfilePicture());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setLikeCount(post.getLikeCount());
        dto.setCommentCount(post.getCommentCount());

        // Mapear comentarios
        List<PostCommentsDTO> comments = new ArrayList<>();
        if (post.getComments() != null) {
            comments = post.getComments().stream().map(comment -> {
                PostCommentsDTO commentDto = new PostCommentsDTO();
                commentDto.setId(comment.getId());
                commentDto.setUsername(comment.getUser().getUsername());
                commentDto.setProfilePicture(comment.getUser().getProfilePicture());
                commentDto.setContent(comment.getCommentText());
                commentDto.setImagenAdjuntada(comment.getImagenAdjuntada());
                commentDto.setCreatedAt(comment.getCreatedAt());
                return commentDto;
            }).collect(Collectors.toList());
        }
        dto.setComments(comments);

        // likedByCurrentUser correctamente seteado
        dto.setLikedByCurrentUser(post.isLikedByCurrentUser());

        return dto;
    }
    
    public PostCommentsDTO toCommentDto(PostComments comment) {
        PostCommentsDTO dto = new PostCommentsDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getCommentText());
        dto.setImagenAdjuntada(comment.getImagenAdjuntada());
        dto.setCreatedAt(comment.getCreatedAt());

        User user = comment.getUser();
        if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setProfilePicture(user.getProfilePicture());
        }

        return dto;
    }

}
