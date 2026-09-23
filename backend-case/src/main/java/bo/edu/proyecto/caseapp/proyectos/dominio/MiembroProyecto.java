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
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(name = "miembros_proyecto", uniqueConstraints = @UniqueConstraint(name = "ux_miembros_proyecto_usuario", columnNames = {"proyecto_id", "usuario_id"}))
public class MiembroProyecto {
    public enum Rol { OWNER, ADMIN, EDITOR, VIEWER }
    public enum Estado { ACTIVE, SUSPENDED, REVOKED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proyecto_id", nullable = false)
    private Long proyectoId;

    @Column(name = "usuario_id", nullable = false, length = 80)
    private String userId;

    @Column(name = "nombre_visible", nullable = false, length = 160)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado status = Estado.ACTIVE;

    @Column(name = "crear_diagrama", nullable = false)
    private boolean createDiagram;

    @Column(name = "editar_modelo", nullable = false)
    private boolean editModel;

    @Column(name = "gestionar_miembros", nullable = false)
    private boolean manageMembers;

    @Column(name = "gestionar_permisos", nullable = false)
    private boolean managePermissions;

    @Column(name = "creado_en", nullable = false)
    private Instant createdAt;

    @Column(name = "actualizado_en", nullable = false)
    private Instant updatedAt;

    protected MiembroProyecto() {}

    public MiembroProyecto(Long proyectoId, String userId, String displayName, Rol role) {
        this.proyectoId = proyectoId;
        this.userId = normalizar(userId);
        this.displayName = displayName == null || displayName.isBlank() ? this.userId : displayName.trim();
        cambiarRol(role == null ? Rol.VIEWER : role);
        this.status = Estado.ACTIVE;
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

    public void actualizar(String displayName, Rol role, Boolean createDiagram, Estado status) {
        if (displayName != null && !displayName.isBlank()) {
            this.displayName = displayName.trim();
        }
        if (role != null) {
            cambiarRol(role);
        }
        if (createDiagram != null) {
            this.createDiagram = createDiagram;
        }
        if (status != null) {
            this.status = status;
        }
    }

    public void cambiarCapacidadCrearDiagrama(boolean permitido) {
        this.createDiagram = permitido;
    }

    public boolean activo() {
        return status == Estado.ACTIVE;
    }

    public boolean administrador() {
        return activo() && (role == Rol.OWNER || role == Rol.ADMIN);
    }

    public void cambiarRol(Rol role) {
        this.role = role;
        this.manageMembers = role == Rol.OWNER || role == Rol.ADMIN;
        this.managePermissions = role == Rol.OWNER || role == Rol.ADMIN;
        this.editModel = role == Rol.OWNER || role == Rol.ADMIN || role == Rol.EDITOR;
        this.createDiagram = role == Rol.OWNER || role == Rol.ADMIN || role == Rol.EDITOR;
    }

    private static String normalizar(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        return valor.trim();
    }

    public Long getId() { return id; }
    public Long getProyectoId() { return proyectoId; }
    public String getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public Rol getRole() { return role; }
    public Estado getStatus() { return status; }
    public boolean isCreateDiagram() { return createDiagram; }
    public boolean isEditModel() { return editModel; }
    public boolean isManageMembers() { return manageMembers; }
    public boolean isManagePermissions() { return managePermissions; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
