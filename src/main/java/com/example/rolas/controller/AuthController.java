package com.example.rolas.controller;

import com.example.rolas.Repositorio.ArtistaRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.dto.LoginRequest;
import com.example.rolas.dto.MensajeResponse;
import com.example.rolas.dto.RegistroRequest;
import com.example.rolas.model.Artista;
import com.example.rolas.model.User;
import com.example.rolas.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Path;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.ui.Model;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepositorio userRepositorio;
    @Autowired
    private ArtistaRepositorio artistaRepositorio; // Agregado ArtistaRepositorio
    
    @Autowired
    private PasswordEncoder passwordEncoder;  // Asegúrate de tener este bean configurado
    private final SecurityContextRepository securityContextRepository = 
        new HttpSessionSecurityContextRepository();
    
   
    @PostMapping("/register")
    public String registerUser(@RequestBody Map<String, Object> request, Model model) {
        String nombre = (String) request.get("nombre");
        String username = (String) request.get("username");
        String email = (String) request.get("email");
        String password = (String) request.get("password");
        String userType = (String) request.get("userType");

        // Crear usuario
        User user = new User(nombre, username, email, passwordEncoder.encode(password));

        System.out.println("Nombre: " + nombre);
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println("UserType: " + userType);

        // Guardar el usuario en la base de datos para obtener el ID
        userRepositorio.save(user);

        // Si es Artista, creamos el Artista con el ID del usuario
        if ("Artista".equals(userType) || "artista".equals(userType)) {
            Artista artista = new Artista();
            artista.setUser(user);  // Asociamos el usuario al artista
            artista.setBio(""); // Asigna una biografía vacía si es necesario
            artista.setVerificado(false); // Por defecto no verificado
            artista.setDiscografica(""); // Por defecto sin discográfica
            
            // Guardar el artista con el user_id asociado
            artistaRepositorio.save(artista);
            
            // Asociamos el artista al usuario, si es necesario
            user.setArtista(artista);
            userRepositorio.save(user);  // Guardar el usuario con su relación a Artista
        }

        // Devolver mensaje de éxito y redirigir al login
        model.addAttribute("message", "Usuario registrado con éxito");
        return "redirect:/login";  // Redirigir al login
    }

    
}