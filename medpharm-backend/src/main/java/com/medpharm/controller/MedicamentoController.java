package com.medpharm.controller;

import com.medpharm.model.Medicamento;
import com.medpharm.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/medicamentos")
public class MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService;

    @GetMapping
    public ResponseEntity<List<Medicamento>> getAllMedicamentos() {
        List<Medicamento> medicamentos = medicamentoService.findAll();
        return ResponseEntity.ok(medicamentos);
    }
}