package bo.edu.proyecto.caseapp.proyectos.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "diagramas_proyecto")
public class DiagramaProyecto {
    public enum Estado { ACTIVE, DELETED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proyecto_id", nullable = false)
    private Long proyectoId;

    @Column(nullable = false, length = 160)
    private String nombre;

    @Column(name = "administrador_usuario_id", nullable = false, length = 80)
    private String administratorUserId;

    @Column(name = "colaboracion_habilitada", nullable = false)
    private boolean collaborationEnabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado estado = Estado.ACTIVE;

    @Column(name = "creado_en", nullable = false)
    private Instant createdAt;

    @Column(name = "actualizado_en", nullable = false)
    private Instant updatedAt;

    protected DiagramaProyecto() {}

    public DiagramaProyecto(Long proyectoId, String nombre, String administratorUserId) {
        this.proyectoId = proyectoId;
        this.nombre = normalizar(nombre, "Vista");
        this.administratorUserId = normalizar(administratorUserId, "");
        this.collaborationEnabled = true;
        this.estado = Estado.ACTIVE;
    }

    @PrePersist
    void antesDeCrear() {
        Instant ahora = Instant.now();
        createdAt = ahora;
        updatedAt = ahora;
    }

    @PreUpdate
    void antesDeActualizar() {
        updatedAt = Instant.now();
    }

    public void cambiarAdministrador(String administratorUserId) {
        this.administratorUserId = normalizar(administratorUserId, this.administratorUserId);
    }

    public void habilitarColaboracion() {
        this.collaborationEnabled = true;
    }

    private static String normalizar(String valor, String fallback) {
        String resuelto = valor == null || valor.isBlank() ? fallback : valor.trim();
        if (resuelto == null || resuelto.isBlank()) {
            throw new IllegalArgumentException("El usuario administrador del diagrama es obligatorio");
        }
        return resuelto;
    }

    public Long getId() { return id; }
    public Long getProyectoId() { return proyectoId; }
    public String getNombre() { return nombre; }
    public String getAdministratorUserId() { return administratorUserId; }
    public boolean isCollaborationEnabled() { return collaborationEnabled; }
    public Estado getEstado() { return estado; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
