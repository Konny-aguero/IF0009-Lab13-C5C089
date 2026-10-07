package com.medpharm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "detalles_receta")
public class DetalleReceta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    @JsonBackReference
    private RecetaMedica receta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "dosis_indicada")
    private String dosisIndicada;

    public DetalleReceta() {
    }

    public DetalleReceta(Medicamento medicamento, Integer cantidad, String dosisIndicada) {
        this.medicamento = medicamento;
        this.cantidad = cantidad;
        this.dosisIndicada = dosisIndicada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RecetaMedica getReceta() {
        return receta;
    }

    public void setReceta(RecetaMedica receta) {
        this.receta = receta;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getDosisIndicada() {
        return dosisIndicada;
    }

    public void setDosisIndicada(String dosisIndicada) {
        this.dosisIndicada = dosisIndicada;
    }
}