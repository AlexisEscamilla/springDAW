package com.example.rolas.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.rolas.Repositorio.UserRepositorio;
import com.example.rolas.model.Post;
import com.example.rolas.model.User;
import com.example.rolas.service.PostService;

@Controller
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostService postService;
    
    @Autowired
    private UserRepositorio userRepositorio;
    
    @GetMapping
    public String getFeed(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User usuario = (User) userDetails; // Asegúrate de que `Usuario` implemente `UserDetails`
        List<Post> posts = postService.getPostsFromFollowing(usuario.getId());
        model.addAttribute("posts", posts);
        return "feed";
    }

    
    @PostMapping
    public String createPost(@RequestParam String contenido,
                             @RequestParam(value = "imageUrl", required = false) MultipartFile imageFile,
                             @AuthenticationPrincipal UserDetails userDetails) {
        User usuario = (User) userDetails;
        String imageUrl = null;

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String uploadsDir = "uploads/";
                String realPathToUploads = new java.io.File(uploadsDir).getAbsolutePath();
                Files.createDirectories(Paths.get(realPathToUploads));
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(realPathToUploads, fileName);
                imageFile.transferTo(filePath);
                imageUrl = fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        postService.createPost(usuario.getId(), contenido, imageUrl);
        return "redirect:/posts";
    }
    

    @PostMapping("/{id}/edit")
    public String editPost(
            @PathVariable Integer id,
            @RequestParam(value = "removeImage", required = false) String removeImageFlag,
            @RequestParam("content") String content,
            @RequestParam(value = "imageUrl", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User usuario = (User) userDetails;
        String imageUrl = null;
        boolean removeImage = "true".equals(removeImageFlag);

        // Si sube una nueva imagen, la guardamos
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String uploadsDir = "uploads/";
                String realPathToUploads = new java.io.File(uploadsDir).getAbsolutePath();
                Files.createDirectories(Paths.get(realPathToUploads));
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(realPathToUploads, fileName);
                imageFile.transferTo(filePath);
                imageUrl = fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Llama al servicio con el flag de eliminar imagen
        postService.editPost(id, usuario.getId(), content, imageUrl, removeImage);

        // Redirige a la página de perfil del usuario
        return "redirect:/mostrarPerfil/" + usuario.getId();
    }
    
    @PostMapping("/{id}/delete")
    public String deletePost(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        postService.deletePost(id, userDetails.getUsername());
        // Busca el usuario por username y redirige usando su id
        User usuario = userRepositorio.findByUsername(userDetails.getUsername()).orElseThrow();
        return "redirect:/mostrarPerfil/" + usuario.getId();
    }

}
