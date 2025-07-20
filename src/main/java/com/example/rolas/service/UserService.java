package com.example.rolas.service;

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

            System.out.println("Contraseña desde cliente: " + loginRequest.getPassword());
            System.out.println("Contraseña en BD (hash): " + user.getPasswordHash());
            System.out.println("Match?: " + passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash()));

            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    
    
    public List<User> getSuggestedUsers(User currentUser, int limit) {
    	 return userRepository.findSuggestedUsers(currentUser.getId(), limit);
    }
}