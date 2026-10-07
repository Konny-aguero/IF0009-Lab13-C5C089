package com.medpharm.service;

import com.medpharm.dto.RecetaRequestDTO;
import com.medpharm.model.*;
import com.medpharm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RecetaService {
    @Autowired private RecetaRepository recetaRepository;
    @Autowired private MedicamentoRepository medicamentoRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    public List<RecetaMedica> findAll() {
        return recetaRepository.findAll();
    }

    public List<RecetaMedica> findByEstado(String estado) {
        if ("TODAS".equals(estado)) return findAll();
        if (!List.of("PENDIENTE", "DESPACHADA", "CANCELADA").contains(estado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado invalido");
        }
        return recetaRepository.findByEstado(estado);
    }

    @Transactional
    public RecetaMedica save(RecetaRequestDTO datos, String username) {
        Usuario medico = usuarioRepository.findByUsername(username).orElseThrow();
        if (!"MEDICO".equals(medico.getRol())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo un medico puede emitir recetas");
        }
        RecetaMedica receta = new RecetaMedica("REC-" + UUID.randomUUID().toString().substring(0, 24),
                datos.pacienteNombre().trim(), medico, "PENDIENTE");
        receta.setFechaEmision(LocalDateTime.now());
        for (var detalle : datos.detalles()) {
            Medicamento medicamento = medicamentoRepository.findById(detalle.medicamentoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicamento no encontrado"));
            if (medicamento.getStock() < detalle.cantidad()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para: " + medicamento.getNombre());
            }
            medicamento.setStock(medicamento.getStock() - detalle.cantidad());
            receta.addDetalle(new DetalleReceta(medicamento, detalle.cantidad(), detalle.dosisIndicada()));
        }
        return recetaRepository.save(receta);
    }

    @Transactional
    public RecetaMedica updateEstado(Long id, String estado) {
        if (!"DESPACHADA".equals(estado) && !"CANCELADA".equals(estado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado invalido");
        }
        RecetaMedica receta = recetaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receta no encontrada"));
        if (!"PENDIENTE".equals(receta.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La receta ya fue procesada");
        }
        if ("CANCELADA".equals(estado)) {
            for (DetalleReceta detalle : receta.getDetalles()) {
                Medicamento medicamento = detalle.getMedicamento();
                medicamento.setStock(medicamento.getStock() + detalle.getCantidad());
            }
        }
        receta.setEstado(estado);
        return recetaRepository.save(receta);
    }
}
