package edu.uam.allport;

import edu.uam.allport.dto.ResultadoTestRequest;
import edu.uam.allport.repository.ResultadoTestRepository;
import edu.uam.allport.service.ResultadoTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ResultadoTestControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ResultadoTestRepository repository;
    @Autowired ResultadoTestService service;

    @BeforeEach
    void preparar() {
        repository.deleteAll();
        for (int i = 1; i <= 7; i++) {
            service.registrar(new ResultadoTestRequest("2024000" + i, "Estudiante " + i, "Sistemas",
                    40, 40, 40, 40, 40, 40));
        }
    }

    @Test
    void postValidoCreaRegistroConDto() throws Exception {
        String json = """
                {"carnet":"20249999","nombreEstudiante":"Ana López","carrera":"Psicología",
                 "teorico":38,"economico":32,"estetico":40,"social":52,"politico":34,"religioso":44,
                 "id":500,"valorDominante":"REL"}
                """;
        mvc.perform(post("/api/v1/resultados").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.carnet").value("20249999"))
                // el cliente no puede imponer campos internos: se ignoran
                .andExpect(jsonPath("$.valorDominante").value("SOC"))
                .andExpect(jsonPath("$.fechaAplicacion").exists());
    }

    @Test
    void postInvalidoDevuelve400ConErrores() throws Exception {
        String json = """
                {"carnet":"ab","nombreEstudiante":"","carrera":"Psicología",
                 "teorico":-5,"economico":32,"estetico":40,"social":52,"politico":34,"religioso":44}
                """;
        mvc.perform(post("/api/v1/resultados").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores", hasKey("carnet")))
                .andExpect(jsonPath("$.errores", hasKey("nombreEstudiante")))
                .andExpect(jsonPath("$.errores", hasKey("teorico")));
    }

    @Test
    void postConSumaDistintaDe240Devuelve400() throws Exception {
        String json = """
                {"carnet":"20248888","nombreEstudiante":"Luis Pérez","carrera":"Derecho",
                 "teorico":40,"economico":40,"estetico":40,"social":40,"politico":40,"religioso":50}
                """;
        mvc.perform(post("/api/v1/resultados").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores", hasKey("sumaValida")));
    }

    @Test
    void postConCarnetDuplicadoDevuelve409() throws Exception {
        String json = """
                {"carnet":"20240001","nombreEstudiante":"Repetido","carrera":"Sistemas",
                 "teorico":40,"economico":40,"estetico":40,"social":40,"politico":40,"religioso":40}
                """;
        mvc.perform(post("/api/v1/resultados").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void getSinParametrosUsaValoresPorDefecto() throws Exception {
        mvc.perform(get("/api/v1/resultados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(7))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void getPagina0YPagina1DevuelvenSubconjuntosDistintos() throws Exception {
        mvc.perform(get("/api/v1/resultados").param("page", "0").param("size", "3"))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].carnet").value("20240001"))
                .andExpect(jsonPath("$.first").value(true));

        mvc.perform(get("/api/v1/resultados").param("page", "1").param("size", "3"))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].carnet").value("20240004"));
    }

    @Test
    void cambiarTamanoCambiaCantidadDeElementos() throws Exception {
        mvc.perform(get("/api/v1/resultados").param("page", "0").param("size", "2"))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalPages").value(4));
    }

    // ---------- Sesión 4 / Taller 1: PUT, DELETE y errores en JSON ----------

    private Long idDe(String carnet) {
        return repository.findAll().stream()
                .filter(r -> r.getCarnet().equals(carnet)).findFirst().orElseThrow().getId();
    }

    @Test
    void putSobreIdExistenteDevuelve200YRecalculaDominante() throws Exception {
        Long id = idDe("20240001");
        String json = """
                {"carnet":"20240001","nombreEstudiante":"Estudiante Uno","carrera":"Psicología",
                 "teorico":30,"economico":30,"estetico":30,"social":30,"politico":30,"religioso":90}
                """;
        // 90 excede el máximo de 80: primero se comprueba el 400
        mvc.perform(put("/api/v1/resultados/" + id).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores", hasKey("religioso")));

        String valido = """
                {"carnet":"20240001","nombreEstudiante":"Estudiante Uno","carrera":"Psicología",
                 "teorico":35,"economico":35,"estetico":35,"social":35,"politico":35,"religioso":65}
                """;
        mvc.perform(put("/api/v1/resultados/" + id).contentType(MediaType.APPLICATION_JSON).content(valido))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.carrera").value("Psicología"))
                .andExpect(jsonPath("$.valorDominante").value("REL"));
    }

    @Test
    void putConCarnetDeOtroRegistroDevuelve409() throws Exception {
        Long id = idDe("20240001");
        String json = """
                {"carnet":"20240002","nombreEstudiante":"Estudiante Uno","carrera":"Sistemas",
                 "teorico":40,"economico":40,"estetico":40,"social":40,"politico":40,"religioso":40}
                """;
        mvc.perform(put("/api/v1/resultados/" + id).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void putSobreIdInexistenteDevuelve404() throws Exception {
        String json = """
                {"carnet":"20247777","nombreEstudiante":"Nadie","carrera":"Sistemas",
                 "teorico":40,"economico":40,"estetico":40,"social":40,"politico":40,"religioso":40}
                """;
        mvc.perform(put("/api/v1/resultados/999999").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe un resultado con id 999999"));
    }

    @Test
    void deleteDevuelve204YLuegoGetDevuelve404() throws Exception {
        Long id = idDe("20240003");
        mvc.perform(delete("/api/v1/resultados/" + id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        mvc.perform(get("/api/v1/resultados/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").exists());
        mvc.perform(delete("/api/v1/resultados/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void idConFormatoInvalidoDevuelve400EnJson() throws Exception {
        mvc.perform(get("/api/v1/resultados/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El parámetro 'id' tiene un formato inválido"));
    }

    @Test
    void metodoNoPermitidoConservaSu405() throws Exception {
        mvc.perform(delete("/api/v1/resultados"))
                .andExpect(status().isMethodNotAllowed());
    }
}
