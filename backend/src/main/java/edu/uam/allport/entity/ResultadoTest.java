package edu.uam.allport.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Resultado de una aplicación del Test de Valores de Allport a un estudiante.
 * id, valorDominante y fechaAplicacion los define el sistema, nunca el cliente.
 */
@Entity
@Table(name = "resultados_test")
public class ResultadoTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String carnet;

    @Column(name = "nombre_estudiante", nullable = false, length = 100)
    private String nombreEstudiante;

    @Column(nullable = false, length = 80)
    private String carrera;

    @Column(nullable = false) private Integer teorico;
    @Column(nullable = false) private Integer economico;
    @Column(nullable = false) private Integer estetico;
    @Column(nullable = false) private Integer social;
    @Column(nullable = false) private Integer politico;
    @Column(nullable = false) private Integer religioso;

    @Column(name = "valor_dominante", nullable = false, length = 3)
    private String valorDominante;

    @Column(name = "fecha_aplicacion", nullable = false, updatable = false)
    private LocalDateTime fechaAplicacion;

    @PrePersist
    void antesDeGuardar() {
        if (fechaAplicacion == null) {
            fechaAplicacion = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public String getCarnet() { return carnet; }
    public void setCarnet(String carnet) { this.carnet = carnet; }
    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }
    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }
    public Integer getTeorico() { return teorico; }
    public void setTeorico(Integer teorico) { this.teorico = teorico; }
    public Integer getEconomico() { return economico; }
    public void setEconomico(Integer economico) { this.economico = economico; }
    public Integer getEstetico() { return estetico; }
    public void setEstetico(Integer estetico) { this.estetico = estetico; }
    public Integer getSocial() { return social; }
    public void setSocial(Integer social) { this.social = social; }
    public Integer getPolitico() { return politico; }
    public void setPolitico(Integer politico) { this.politico = politico; }
    public Integer getReligioso() { return religioso; }
    public void setReligioso(Integer religioso) { this.religioso = religioso; }
    public String getValorDominante() { return valorDominante; }
    public void setValorDominante(String valorDominante) { this.valorDominante = valorDominante; }
    public LocalDateTime getFechaAplicacion() { return fechaAplicacion; }
}
