package com.medpharm.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record RecetaRequestDTO(
        @NotBlank @Size(min = 5, max = 120) String pacienteNombre,
        @NotEmpty List<@NotNull @Valid DetalleDTO> detalles) {
    public record DetalleDTO(
            @NotNull Long medicamentoId,
            @NotNull @Positive Integer cantidad,
            @NotBlank @Size(max = 255) String dosisIndicada) {}
}
