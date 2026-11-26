package edu.uam.allport.dto;

import java.time.LocalDateTime;

/** DTO de salida: lo que la API devuelve de un resultado. */
public record ResultadoTestResponse(
        Long id,
        String carnet,
        String nombreEstudiante,
        String carrera,
        Integer teorico,
        Integer economico,
        Integer estetico,
        Integer social,
        Integer politico,
        Integer religioso,
        String valorDominante,
        LocalDateTime fechaAplicacion
) {
}
