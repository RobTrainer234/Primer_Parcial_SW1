package bo.edu.proyecto.caseapp.autenticacion.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_academicos")
public class UsuarioAcademico {

    @Id
    @Column(name = "usuario_id", nullable = false, length = 80)
    private String usuarioId;

    @Column(name = "nombre_visible", nullable = false, length = 160)
    private String nombreVisible;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    protected UsuarioAcademico() {
    }

    public UsuarioAcademico(String usuarioId, String nombreVisible, boolean activo) {
        this.usuarioId = usuarioId;
        this.nombreVisible = nombreVisible;
        this.activo = activo;
    }

    @PrePersist
    void antesDeCrear() {
        LocalDateTime ahora = LocalDateTime.now();
        creadoEn = ahora;
        actualizadoEn = ahora;
    }

    @PreUpdate
    void antesDeActualizar() {
        actualizadoEn = LocalDateTime.now();
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }

    public boolean isActivo() {
        return activo;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }
}
