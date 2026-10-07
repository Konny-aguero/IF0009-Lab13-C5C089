package com.medpharm.service;

import com.medpharm.model.DetalleReceta;
import com.medpharm.model.Medicamento;
import com.medpharm.model.RecetaMedica;
import com.medpharm.repository.MedicamentoRepository;
import com.medpharm.repository.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecetaService {

    @Autowired
    private RecetaRepository recetaRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    public List<RecetaMedica> findAll() {
        return recetaRepository.findAll();
    }

    public List<RecetaMedica> findByEstado(String estado) {
        return recetaRepository.findByEstado(estado);
    }

    @Transactional
    public RecetaMedica save(RecetaMedica recetaMedica) {
        recetaMedica.setFechaEmision(LocalDateTime.now());
        recetaMedica.setEstado("PENDIENTE");

        if (recetaMedica.getDetalles() != null) {
            for (DetalleReceta detalle : recetaMedica.getDetalles()) {
                Medicamento medicamento = medicamentoRepository.findById(detalle.getMedicamento().getId())
                        .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));

                if (medicamento.getStock() < detalle.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para: " + medicamento.getNombre());
                }

                medicamento.setStock(medicamento.getStock() - detalle.getCantidad());
                medicamentoRepository.save(medicamento);

                detalle.setMedicamento(medicamento);
                detalle.setReceta(recetaMedica);
            }
        }
        return recetaRepository.save(recetaMedica);
    }

    @Transactional
    public RecetaMedica updateEstado(Long id, String estado) {
        RecetaMedica receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        receta.setEstado(estado);
        return recetaRepository.save(receta);
    }
}