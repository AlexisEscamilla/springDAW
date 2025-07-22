package com.example.rolas.controller;

import com.example.rolas.model.User;
import com.example.rolas.service.UserService;
import com.example.rolas.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UsersRestController {

    @Autowired
    private UserService userService;

    // GET /api/users/suggestions - Usuarios sugeridos
    @GetMapping("/users/suggestions")
    public ResponseEntity<?> getSuggestions(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userService.findByUsername(username);
        List<User> suggestions = userService.getSuggestedUsers(user, 5);
        return ResponseEntity.ok(suggestions);
    }

    // GET /api/user/profile - Perfil del usuario autenticado
    @GetMapping("/user/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    // POST /api/users/{id}/follow - Seguir usuario
    @PostMapping("/users/{id}/follow")
    public ResponseEntity<?> followUser(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") int userIdToFollow
    ) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        User currentUser = userService.findByUsername(username);
        boolean following = userService.toggleFollow(currentUser.getId(), userIdToFollow);
        return ResponseEntity.ok(Map.of("following", following));
    }
}