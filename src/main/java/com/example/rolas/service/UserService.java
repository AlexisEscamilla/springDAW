package com.example.rolas.service;

import com.example.rolas.model.Followers;
import com.example.rolas.model.User;
import com.example.rolas.DTO.RegistroRequest;
import com.example.rolas.Repositorio.*;
import com.example.rolas.controller.LoginRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepositorio userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private FollowersRepositorio followerRepositorio;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    
    public void save(User user) {
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    public User findById(int id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public User registerUser(RegistroRequest registerRequest) {
        // crea nuevo usuario
        User user = new User(
        		registerRequest.getNombre(),
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword())
        );
        
        return userRepository.save(user);
    }
    
    public Optional<User> authenticateUser(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }
    
    
    public List<User> getSuggestedUsers(User currentUser, int limit) {
    	 return userRepository.findSuggestedUsers(currentUser.getId(), limit);
    }
    
    public boolean toggleFollow(int currentUserId, int userIdToFollow) {
        if (currentUserId == userIdToFollow) return false; // No puede seguirse a sí mismo

        Followers currentUser = followerRepositorio.findById(currentUserId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Followers userToFollow = followerRepositorio.findById(userIdToFollow)
            .orElseThrow(() -> new RuntimeException("Usuario a seguir no encontrado"));

        Optional<User> usuarioSeguir = userRepository.findById(currentUser.getId());
        User user = usuarioSeguir.get();
        // Suponiendo que tienes una relación Set<User> following en User
        if (currentUser.getFollowing().equals(userToFollow)) {
            followerRepositorio.delete(currentUser);
            return false; // Ya no lo sigue
        } else {
            currentUser.setFollowing(user);
            followerRepositorio.save(currentUser);
            return true; // Ahora lo sigue
        }
    }

    
}