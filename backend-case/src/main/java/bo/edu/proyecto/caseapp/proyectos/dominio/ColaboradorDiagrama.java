package bo.edu.proyecto.caseapp.proyectos.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(name = "colaboradores_diagrama", uniqueConstraints = @UniqueConstraint(name = "ux_colaboradores_diagrama_usuario", columnNames = {"diagrama_id", "usuario_id"}))
public class ColaboradorDiagrama {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diagrama_id", nullable = false)
    private Long diagramaId;

    @Column(name = "usuario_id", nullable = false, length = 80)
    private String userId;

    @Column(nullable = false, length = 30)
    private String role;

    @Column(name = "puede_editar", nullable = false)
    private boolean canEdit;

    @Column(name = "puede_comentar", nullable = false)
    private boolean canComment;

    @Column(name = "creado_en", nullable = false)
    private Instant createdAt;

    @Column(name = "actualizado_en", nullable = false)
    private Instant updatedAt;

    protected ColaboradorDiagrama() {}

    public ColaboradorDiagrama(Long diagramaId, String userId, String role, boolean canEdit, boolean canComment) {
        this.diagramaId = diagramaId;
        this.userId = normalizar(userId);
        actualizar(role, canEdit, canComment);
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

    public void actualizar(String role, boolean canEdit, boolean canComment) {
        this.role = role == null || role.isBlank() ? "EDITOR" : role.trim().toUpperCase();
        this.canEdit = canEdit;
        this.canComment = canComment;
    }

    public boolean administrador() {
        return "ADMIN".equalsIgnoreCase(role) || "OWNER".equalsIgnoreCase(role);
    }

    private static String normalizar(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El usuario colaborador es obligatorio");
        }
        return valor.trim();
    }

    public Long getId() { return id; }
    public Long getDiagramaId() { return diagramaId; }
    public String getUserId() { return userId; }
    public String getRole() { return role; }
    public boolean isCanEdit() { return canEdit; }
    public boolean isCanComment() { return canComment; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
