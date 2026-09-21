package bo.edu.proyecto.caseapp.artefactos.dominio;

import bo.edu.proyecto.caseapp.generacion.dominio.TrabajoGeneracion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "artefactos_generados")
public class ArtefactoGenerado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trabajo_generacion_id", nullable = false)
    private TrabajoGeneracion trabajoGeneracion;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "ruta_archivo", nullable = false, length = 1000)
    private String rutaArchivo;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    protected ArtefactoGenerado() {
    }

    public ArtefactoGenerado(TrabajoGeneracion trabajoGeneracion, String nombreArchivo, String rutaArchivo) {
        this.trabajoGeneracion = trabajoGeneracion;
        this.nombreArchivo = nombreArchivo;
        this.rutaArchivo = rutaArchivo;
    }

    @PrePersist
    void antesDeCrear() {
        creadoEn = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public TrabajoGeneracion getTrabajoGeneracion() { return trabajoGeneracion; }
    public String getNombreArchivo() { return nombreArchivo; }
    public String getRutaArchivo() { return rutaArchivo; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}
