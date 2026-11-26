package edu.uam.allport.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para registrar un resultado del Test de Valores de Allport.
 * Solo contiene lo que el cliente debe enviar: no incluye id, valorDominante
 * ni fechaAplicacion, que los genera el sistema.
 */
public record ResultadoTestRequest(

        @NotBlank(message = "El carnet es obligatorio")
        @Pattern(regexp = "^[0-9]{5,20}$", message = "El carnet debe tener solo dígitos (entre 5 y 20)")
        String carnet,

        @NotBlank(message = "El nombre del estudiante es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombreEstudiante,

        @NotBlank(message = "La carrera es obligatoria")
        @Size(min = 3, max = 80, message = "La carrera debe tener entre 3 y 80 caracteres")
        String carrera,

        @NotNull(message = "El puntaje teórico es obligatorio")
        @Min(value = 0, message = "El puntaje teórico no puede ser negativo")
        @Max(value = 80, message = "El puntaje teórico no puede ser mayor que 80")
        Integer teorico,

        @NotNull(message = "El puntaje económico es obligatorio")
        @Min(value = 0, message = "El puntaje económico no puede ser negativo")
        @Max(value = 80, message = "El puntaje económico no puede ser mayor que 80")
        Integer economico,

        @NotNull(message = "El puntaje estético es obligatorio")
        @Min(value = 0, message = "El puntaje estético no puede ser negativo")
        @Max(value = 80, message = "El puntaje estético no puede ser mayor que 80")
        Integer estetico,

        @NotNull(message = "El puntaje social es obligatorio")
        @Min(value = 0, message = "El puntaje social no puede ser negativo")
        @Max(value = 80, message = "El puntaje social no puede ser mayor que 80")
        Integer social,

        @NotNull(message = "El puntaje político es obligatorio")
        @Min(value = 0, message = "El puntaje político no puede ser negativo")
        @Max(value = 80, message = "El puntaje político no puede ser mayor que 80")
        Integer politico,

        @NotNull(message = "El puntaje religioso es obligatorio")
        @Min(value = 0, message = "El puntaje religioso no puede ser negativo")
        @Max(value = 80, message = "El puntaje religioso no puede ser mayor que 80")
        Integer religioso
) {

    /** En el Estudio de Valores de Allport-Vernon-Lindzey los seis puntajes suman 240. */
    public static final int SUMA_TOTAL = 240;

    @JsonIgnore
    @AssertTrue(message = "La suma de los seis puntajes debe ser 240")
    public boolean isSumaValida() {
        if (teorico == null || economico == null || estetico == null
                || social == null || politico == null || religioso == null) {
            return true; // los @NotNull ya reportan el campo faltante
        }
        return teorico + economico + estetico + social + politico + religioso == SUMA_TOTAL;
    }
}
