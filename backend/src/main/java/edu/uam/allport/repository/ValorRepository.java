package edu.uam.allport.repository;

import edu.uam.allport.entity.Valor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValorRepository extends JpaRepository<Valor, Long> {

    Page<Valor> findByNombreContainingIgnoreCaseOrCodigoContainingIgnoreCase(
            String nombre, String codigo, Pageable pageable);
}
