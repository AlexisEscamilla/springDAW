package com.example.rolas.controller;


import com.example.rolas.Repositorio.ArtistaRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.config.JwtUtil;
import com.example.rolas.model.Artista;
import com.example.rolas.model.User;
import com.example.rolas.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {
    
    @Autowired
    private UserRepositorio userRepositorio;
    @Autowired
    private ArtistaRepositorio artistaRepositorio; 
    
    @Autowired
    private PasswordEncoder passwordEncoder;  
    
    @Autowired
	private UserService userService;
    
    @PostMapping("/register")
    public String registerUser(@RequestBody Map<String, Object> request, Model model) {
        String nombre = (String) request.get("nombre");
        String username = (String) request.get("username");
        String email = (String) request.get("email");
        String password = (String) request.get("password");
        String userType = (String) request.get("userType");

        // se crea usuario
        User user = new User(nombre, username, email, passwordEncoder.encode(password));

        System.out.println("Nombre: " + nombre);
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println("UserType: " + userType);

        // guarda el usuario en la base de datos 
        userRepositorio.save(user);

        // Si es Artista, creamos el Artista con el ID del usuario
        if ("Artista".equals(userType) || "artista".equals(userType)) {
            Artista artista = new Artista();
            artista.setUser(user); 
            artista.setBio(""); 
            artista.setVerificado(false);
            artista.setDiscografica(""); 
            
           
            artistaRepositorio.save(artista);
            
            // Asociamos el artista al usuario, si es necesario
            user.setArtista(artista);
            userRepositorio.save(user); 
        }

        // Devuelve mensaje de exito y redirigir al login
        model.addAttribute("message", "Usuario registrado con éxito");
        return "redirect:/login"; 
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userService.authenticateUser(loginRequest);

        if (userOptional.isPresent()) {
            String token = JwtUtil.generateToken(loginRequest.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", loginRequest.getUsername());
            
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }
}