package edu.uam.allport.config;

import edu.uam.allport.dto.ResultadoTestRequest;
import edu.uam.allport.entity.Valor;
import edu.uam.allport.repository.ResultadoTestRepository;
import edu.uam.allport.repository.ValorRepository;
import edu.uam.allport.service.ResultadoTestService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * Carga datos de ejemplo si las tablas están vacías, para poder probar
 * la paginación (página 0 y 1) desde el primer arranque.
 */
@Configuration
@Profile("!test")
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarDatos(ValorRepository valores, ResultadoTestRepository resultados,
                                  ResultadoTestService service) {
        return args -> {
            if (valores.count() == 0) {
                valores.saveAll(List.of(
                        new Valor("TEO", "Teórico", "Interés por descubrir la verdad y ordenar el conocimiento."),
                        new Valor("ECO", "Económico", "Interés por lo útil y práctico, y por la eficiencia en los recursos."),
                        new Valor("EST", "Estético", "Interés por la forma, la armonía y la belleza de las experiencias."),
                        new Valor("SOC", "Social", "Interés por las personas y su bienestar."),
                        new Valor("POL", "Político", "Interés por el poder, la influencia y el liderazgo."),
                        new Valor("REL", "Religioso", "Interés por la unidad, el sentido y la trascendencia.")));
            }
            if (resultados.count() == 0) {
                // Datos ficticios de ejemplo; cada fila suma 240.
                List.of(
                        new ResultadoTestRequest("20230001", "Ana López", "Ingeniería en Sistemas", 48, 42, 35, 40, 38, 37),
                        new ResultadoTestRequest("20230002", "Carlos Méndez", "Administración de Empresas", 36, 50, 30, 38, 46, 40),
                        new ResultadoTestRequest("20230003", "María Torres", "Psicología", 38, 32, 40, 52, 34, 44),
                        new ResultadoTestRequest("20230004", "José Ruiz", "Arquitectura", 40, 36, 52, 38, 36, 38),
                        new ResultadoTestRequest("20230005", "Lucía Pérez", "Derecho", 37, 39, 34, 42, 50, 38),
                        new ResultadoTestRequest("20230006", "Diego Castillo", "Ingeniería Civil", 46, 44, 36, 36, 40, 38),
                        new ResultadoTestRequest("20230007", "Sofía Ramírez", "Medicina", 44, 34, 38, 48, 32, 44),
                        new ResultadoTestRequest("20230008", "Andrés Morales", "Ingeniería en Sistemas", 50, 40, 34, 36, 42, 38),
                        new ResultadoTestRequest("20230009", "Valeria Gómez", "Psicología", 36, 30, 42, 50, 36, 46),
                        new ResultadoTestRequest("20230010", "Miguel Herrera", "Administración de Empresas", 34, 52, 32, 36, 48, 38),
                        new ResultadoTestRequest("20230011", "Camila Vargas", "Arquitectura", 38, 34, 54, 40, 34, 40),
                        new ResultadoTestRequest("20230012", "Daniel Flores", "Teología", 36, 32, 38, 44, 34, 56)
                ).forEach(service::registrar);
            }
        };
    }
}
