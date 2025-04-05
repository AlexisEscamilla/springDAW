package com.example.rolas.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rolas.model.Albums;
import com.example.rolas.model.Artista;

@Repository
public interface ArtistaRepositorio extends JpaRepository<Artista, Integer> {
    Optional<Artista> findById(Integer id);
    Optional<Artista> findByUserId(Integer userId);
  
}

