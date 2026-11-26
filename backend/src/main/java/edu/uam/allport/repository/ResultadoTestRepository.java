package edu.uam.allport.repository;

import edu.uam.allport.entity.ResultadoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultadoTestRepository extends JpaRepository<ResultadoTest, Long> {

    boolean existsByCarnet(String carnet);

    /** Para el PUT: el carnet no puede repetirse en otro registro distinto al que se actualiza. */
    boolean existsByCarnetAndIdNot(String carnet, Long id);

    /** Filtro opcional por carrera; la paginación la resuelve la base de datos (LIMIT/OFFSET). */
    Page<ResultadoTest> findByCarreraContainingIgnoreCase(String carrera, Pageable pageable);
}
