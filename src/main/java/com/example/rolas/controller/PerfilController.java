package com.example.rolas.controller;

import com.example.rolas.model.User;
import com.example.rolas.Repositorio.FollowersRepositorio;
import com.example.rolas.Repositorio.UserRepositorio;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
public class PerfilController {

    @Autowired
    private UserRepositorio userRepositorio;

    @Autowired
    private FollowersRepositorio followersRepositorio;
    
    @GetMapping("/perfil")
    public String verPerfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User usuario = userRepositorio.findByUsername(userDetails.getUsername()).orElse(null);

        // numero de seguidos
        int siguiendo = followersRepositorio.countByFollower(usuario);

        // numero de seguidores 
        int seguidores = followersRepositorio.countByFollowing(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("siguiendo", siguiendo);
        model.addAttribute("seguidores", seguidores);

        return "perfil";
    }
    
    @PostMapping("/perfil")
    public String actualizarPerfil(@ModelAttribute("usuario") User usuarioForm,
                                   @RequestParam(value = "imagen", required = false) MultipartFile imagen,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {

        // busca usuario autenticado
        Optional<User> usuarioOpt = userRepositorio.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        User usuarioActual = usuarioOpt.get();

        // actualiza datos comunes
        usuarioActual.setNombre(usuarioForm.getNombre());
        usuarioActual.setUsername(usuarioForm.getUsername());
        usuarioActual.setEmail(usuarioForm.getEmail());

        // actualiza biografia si es Artista
        if (usuarioActual.getArtista() != null && usuarioForm.getArtista() != null) {
            usuarioActual.getArtista().setBio(usuarioForm.getArtista().getBio());
        }

        // procesa imagen
        if (imagen != null && !imagen.isEmpty()) {
            try {
                String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
                String uploadDir = new File("uploads").getAbsolutePath();
                new File(uploadDir).mkdirs();

                File destino = new File(uploadDir + File.separator + nombreArchivo);
                imagen.transferTo(destino);

                usuarioActual.setProfilePicture(nombreArchivo);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "Error al guardar la imagen.");
                return "perfil";
            }
        }

        // guardar cambios
        userRepositorio.save(usuarioActual);

        return "redirect:/feed";
    }
    
    
    @PostMapping("/perfil/eliminar")
    public String eliminarCuenta(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        Optional<User> usuarioOpt = userRepositorio.findByUsername(userDetails.getUsername());
        if (usuarioOpt.isPresent()) {
            User usuario = usuarioOpt.get();
            userRepositorio.delete(usuario);
            request.getSession().invalidate(); 
        }
        return "redirect:/login";
    }



    
}
