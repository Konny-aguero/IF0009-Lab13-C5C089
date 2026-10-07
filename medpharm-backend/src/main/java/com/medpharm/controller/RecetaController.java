package com.medpharm.controller;

import com.medpharm.model.RecetaMedica;
import com.medpharm.dto.RecetaRequestDTO;
import jakarta.validation.Valid;
import java.security.Principal;
import com.medpharm.service.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/recetas")
public class RecetaController {

    @Autowired
    private RecetaService recetaService;

    // Obtener todas las recetas
    @GetMapping
    public ResponseEntity<List<RecetaMedica>> getAllRecetas() {
        return ResponseEntity.ok(recetaService.findAll());
    }

    // Filtrar recetas por estado (TODAS, PENDIENTE, DESPACHADA)
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<RecetaMedica>> getRecetasByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(recetaService.findByEstado(estado));
    }

    // Registrar una nueva receta (Aquí el Service valida el stock)
    @PostMapping
    public ResponseEntity<RecetaMedica> createReceta(@Valid @RequestBody RecetaRequestDTO recetaMedica, Principal principal) {
        RecetaMedica nuevaReceta = recetaService.save(recetaMedica, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReceta);
    }

    // Modificar el estado de la receta
    @PatchMapping("/{id}/estado")
    public ResponseEntity<RecetaMedica> updateEstado(
            @PathVariable Long id, 
            @RequestBody Map<String, String> requestBody) {
        
        String nuevoEstado = requestBody.get("estado"); // Extrae "DESPACHADA" o "CANCELADA"
        RecetaMedica recetaActualizada = recetaService.updateEstado(id, nuevoEstado);
        
        return ResponseEntity.ok(recetaActualizada);
    }
}