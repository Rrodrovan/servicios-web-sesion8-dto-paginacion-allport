package edu.uam.allport.controller;

import edu.uam.allport.dto.PaginaResponse;
import edu.uam.allport.dto.ValorResponse;
import edu.uam.allport.service.ValorService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Catálogo de valores (lo consume el frontend React: GET /api/v1/valores?q=). */
@RestController
@RequestMapping("/api/v1/valores")
public class ValorController {

    private final ValorService service;

    public ValorController(ValorService service) {
        this.service = service;
    }

    @GetMapping
    public PaginaResponse<ValorResponse> listar(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.listar(q, pageable);
    }

    @GetMapping("/{id}")
    public ValorResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }
}
