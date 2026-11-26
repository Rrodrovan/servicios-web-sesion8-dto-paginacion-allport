package edu.uam.allport.service;

import edu.uam.allport.dto.PaginaResponse;
import edu.uam.allport.dto.ResultadoTestRequest;
import edu.uam.allport.dto.ResultadoTestResponse;
import edu.uam.allport.entity.ResultadoTest;
import edu.uam.allport.exception.RecursoDuplicadoException;
import edu.uam.allport.exception.RecursoNoEncontradoException;
import edu.uam.allport.repository.ResultadoTestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lógica de negocio de los resultados del test: convierte DTO <-> entidad,
 * calcula el valor dominante y aplica las reglas antes de persistir.
 */
@Service
public class ResultadoTestService {

    private final ResultadoTestRepository repository;

    public ResultadoTestService(ResultadoTestRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ResultadoTestResponse registrar(ResultadoTestRequest request) {
        String carnet = request.carnet().trim();
        if (repository.existsByCarnet(carnet)) {
            throw new RecursoDuplicadoException("Ya existe un resultado registrado para el carnet " + carnet);
        }
        ResultadoTest entidad = aEntidad(request);
        return aResponse(repository.save(entidad));
    }

    @Transactional(readOnly = true)
    public PaginaResponse<ResultadoTestResponse> listar(String carrera, Pageable pageable) {
        Page<ResultadoTest> pagina = (carrera == null || carrera.isBlank())
                ? repository.findAll(pageable)
                : repository.findByCarreraContainingIgnoreCase(carrera.trim(), pageable);
        return PaginaResponse.de(pagina, this::aResponse);
    }

    @Transactional(readOnly = true)
    public ResultadoTestResponse obtener(Long id) {
        return aResponse(buscar(id));
    }

    @Transactional
    public ResultadoTestResponse actualizar(Long id, ResultadoTestRequest request) {
        ResultadoTest entidad = buscar(id);
        String carnet = request.carnet().trim();
        if (repository.existsByCarnetAndIdNot(carnet, id)) {
            throw new RecursoDuplicadoException("El carnet " + carnet + " ya pertenece a otro resultado");
        }
        copiarDatos(request, entidad);
        return aResponse(repository.save(entidad));
    }

    @Transactional
    public void eliminar(Long id) {
        repository.delete(buscar(id));
    }

    private ResultadoTest buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un resultado con id " + id));
    }

    // ---------- Conversión DTO <-> entidad ----------

    private ResultadoTest aEntidad(ResultadoTestRequest r) {
        ResultadoTest e = new ResultadoTest();
        copiarDatos(r, e);
        return e;
    }

    /** Copia los datos del DTO sobre la entidad; id y fechaAplicacion no cambian. */
    private void copiarDatos(ResultadoTestRequest r, ResultadoTest e) {
        e.setCarnet(r.carnet().trim());
        e.setNombreEstudiante(r.nombreEstudiante().trim());
        e.setCarrera(r.carrera().trim());
        e.setTeorico(r.teorico());
        e.setEconomico(r.economico());
        e.setEstetico(r.estetico());
        e.setSocial(r.social());
        e.setPolitico(r.politico());
        e.setReligioso(r.religioso());
        e.setValorDominante(calcularValorDominante(r));
    }

    private ResultadoTestResponse aResponse(ResultadoTest e) {
        return new ResultadoTestResponse(
                e.getId(), e.getCarnet(), e.getNombreEstudiante(), e.getCarrera(),
                e.getTeorico(), e.getEconomico(), e.getEstetico(),
                e.getSocial(), e.getPolitico(), e.getReligioso(),
                e.getValorDominante(), e.getFechaAplicacion());
    }

    /** Código del valor con mayor puntaje; en empate gana el primero en el orden del test. */
    static String calcularValorDominante(ResultadoTestRequest r) {
        Map<String, Integer> puntajes = new LinkedHashMap<>();
        puntajes.put("TEO", r.teorico());
        puntajes.put("ECO", r.economico());
        puntajes.put("EST", r.estetico());
        puntajes.put("SOC", r.social());
        puntajes.put("POL", r.politico());
        puntajes.put("REL", r.religioso());

        String dominante = null;
        int mayor = Integer.MIN_VALUE;
        for (Map.Entry<String, Integer> p : puntajes.entrySet()) {
            if (p.getValue() > mayor) {
                mayor = p.getValue();
                dominante = p.getKey();
            }
        }
        return dominante;
    }
}
