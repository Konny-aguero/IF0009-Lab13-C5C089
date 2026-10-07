package com.medpharm.repository;

import com.medpharm.model.RecetaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<RecetaMedica, Long> {
    List<RecetaMedica> findByEstado(String estado);
}