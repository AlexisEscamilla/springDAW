package com.example.rolas.RestController;

import com.example.rolas.model.Post;
import com.example.rolas.model.User;
import com.example.rolas.service.UserService;
import com.example.rolas.DTO.PostDTO;
import com.example.rolas.DTO.PostMapper;
import com.example.rolas.DTO.UserDTO;
import com.example.rolas.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping("/user/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userService.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        UserDTO userDTO = new UserDTO(user);
        return ResponseEntity.ok(userDTO);
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