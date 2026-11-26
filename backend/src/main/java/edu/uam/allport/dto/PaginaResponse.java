package edu.uam.allport.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Respuesta paginada estable (no expone PageImpl de Spring).
 * number = página devuelta (base 0), size = tamaño pedido.
 */
public record PaginaResponse<T>(
        List<T> content,
        int number,
        int size,
        int numberOfElements,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    public static <E, T> PaginaResponse<T> de(Page<E> pagina, Function<E, T> mapper) {
        return new PaginaResponse<>(
                pagina.getContent().stream().map(mapper).toList(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getNumberOfElements(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isFirst(),
                pagina.isLast()
        );
    }
}
