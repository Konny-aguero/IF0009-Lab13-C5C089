package com.medpharm.service;

import com.medpharm.model.Medicamento;
import com.medpharm.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    public List<Medicamento> findAll() {
        return medicamentoRepository.findAll();
    }
}