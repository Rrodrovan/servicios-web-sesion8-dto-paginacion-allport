package edu.uam.allport.controller;

import edu.uam.allport.dto.PaginaResponse;
import edu.uam.allport.dto.ResultadoTestRequest;
import edu.uam.allport.dto.ResultadoTestResponse;
import edu.uam.allport.service.ResultadoTestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Controller delgado: recibe HTTP, valida el DTO con @Valid y delega al Service.
 */
@RestController
@RequestMapping("/api/v1/resultados")
public class ResultadoTestController {

    private final ResultadoTestService service;

    public ResultadoTestController(ResultadoTestService service) {
        this.service = service;
    }

    /** Registro mediante DTO: nunca recibe la entidad persistente. */
    @PostMapping
    public ResponseEntity<ResultadoTestResponse> registrar(@Valid @RequestBody ResultadoTestRequest request) {
        ResultadoTestResponse creado = service.registrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    /**
     * Listado paginado: ?page=0&size=5&sort=id,asc (opcional ?carrera=...).
     * Sin parámetros devuelve la página 0 con 5 registros ordenados por id.
     */
    @GetMapping
    public PaginaResponse<ResultadoTestResponse> listar(
            @RequestParam(required = false) String carrera,
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.listar(carrera, pageable);
    }

    @GetMapping("/{id}")
    public ResultadoTestResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }
}
