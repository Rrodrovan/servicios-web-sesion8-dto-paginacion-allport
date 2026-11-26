package edu.uam.allport.service;

import edu.uam.allport.dto.PaginaResponse;
import edu.uam.allport.dto.ValorResponse;
import edu.uam.allport.entity.Valor;
import edu.uam.allport.exception.RecursoNoEncontradoException;
import edu.uam.allport.repository.ValorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ValorService {

    private final ValorRepository repository;

    public ValorService(ValorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PaginaResponse<ValorResponse> listar(String q, Pageable pageable) {
        Page<Valor> pagina = (q == null || q.isBlank())
                ? repository.findAll(pageable)
                : repository.findByNombreContainingIgnoreCaseOrCodigoContainingIgnoreCase(q.trim(), q.trim(), pageable);
        return PaginaResponse.de(pagina, this::aResponse);
    }

    @Transactional(readOnly = true)
    public ValorResponse obtener(Long id) {
        return repository.findById(id)
                .map(this::aResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un valor con id " + id));
    }

    private ValorResponse aResponse(Valor v) {
        return new ValorResponse(v.getId(), v.getCodigo(), v.getNombre(), v.getDescripcion());
    }
}
