package bo.edu.proyecto.caseapp.generacion.dominio;

import bo.edu.proyecto.caseapp.modelado.dominio.ModeloConceptual;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "trabajos_generacion")
public class TrabajoGeneracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modelo_id", nullable = false)
    private ModeloConceptual modelo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EstadoGeneracion estado;

    @Column(name = "iniciado_en")
    private LocalDateTime iniciadoEn;

    @Column(name = "finalizado_en")
    private LocalDateTime finalizadoEn;

    @Column(name = "mensaje_error", length = 2000)
    private String mensajeError;

    protected TrabajoGeneracion() {
    }

    public TrabajoGeneracion(ModeloConceptual modelo) {
        this.modelo = modelo;
        this.estado = EstadoGeneracion.EN_PROCESO;
    }

    @PrePersist
    void antesDeCrear() {
        if (iniciadoEn == null) {
            iniciadoEn = LocalDateTime.now();
        }
    }

    public void completar() {
        this.estado = EstadoGeneracion.COMPLETADO;
        this.finalizadoEn = LocalDateTime.now();
    }

    public void fallar(String mensajeError) {
        this.estado = EstadoGeneracion.FALLIDO;
        this.mensajeError = mensajeError;
        this.finalizadoEn = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public ModeloConceptual getModelo() { return modelo; }
    public EstadoGeneracion getEstado() { return estado; }
    public LocalDateTime getIniciadoEn() { return iniciadoEn; }
    public LocalDateTime getFinalizadoEn() { return finalizadoEn; }
    public String getMensajeError() { return mensajeError; }
}
