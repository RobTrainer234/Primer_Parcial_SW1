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
@Table(name = "invitaciones_proyecto")
public class InvitacionProyecto {
    public enum Estado { PENDING, ACCEPTED, REJECTED, REVOKED, EXPIRED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proyecto_id", nullable = false)
    private Long proyectoId;

    @Column(name = "invitado_usuario_id", nullable = false, length = 80)
    private String inviteeUserId;

    @Column(name = "invitador_usuario_id", nullable = false, length = 80)
    private String inviterUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MiembroProyecto.Rol role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado status = Estado.PENDING;

    @Column(nullable = false, length = 128)
    private String token;

    @Column(name = "expira_en", nullable = false)
    private Instant expiresAt;

    @Column(name = "creado_en", nullable = false)
    private Instant createdAt;

    @Column(name = "actualizado_en", nullable = false)
    private Instant updatedAt;

    protected InvitacionProyecto() {}

    public InvitacionProyecto(Long proyectoId, String inviteeUserId, String inviterUserId, MiembroProyecto.Rol role, String token, Instant expiresAt) {
        this.proyectoId = proyectoId;
        this.inviteeUserId = inviteeUserId.trim();
        this.inviterUserId = inviterUserId.trim();
        this.role = role == null ? MiembroProyecto.Rol.VIEWER : role;
        this.token = token;
        this.expiresAt = expiresAt;
        this.status = Estado.PENDING;
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

    public boolean pendienteVigente(Instant ahora) {
        return status == Estado.PENDING && expiresAt.isAfter(ahora);
    }

    public void aceptar() { status = Estado.ACCEPTED; }
    public void rechazar() { status = Estado.REJECTED; }
    public void revocar() { status = Estado.REVOKED; }
    public void expirar() { status = Estado.EXPIRED; }

    public Long getId() { return id; }
    public Long getProyectoId() { return proyectoId; }
    public String getInviteeUserId() { return inviteeUserId; }
    public String getInviterUserId() { return inviterUserId; }
    public MiembroProyecto.Rol getRole() { return role; }
    public Estado getStatus() { return status; }
    public String getToken() { return token; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
