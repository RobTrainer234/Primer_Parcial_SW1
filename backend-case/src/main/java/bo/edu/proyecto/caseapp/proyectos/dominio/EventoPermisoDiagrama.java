package bo.edu.proyecto.caseapp.proyectos.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "eventos_permiso_diagrama")
public class EventoPermisoDiagrama {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proyecto_id", nullable = false)
    private Long proyectoId;

    @Column(name = "diagrama_id", nullable = false)
    private Long diagramaId;

    @Column(name = "actor_usuario_id", nullable = false, length = 80)
    private String actorUserId;

    @Column(nullable = false, length = 80)
    private String action;

    @Column(length = 160)
    private String target;

    @Column(name = "antes", columnDefinition = "TEXT")
    private String beforeValue;

    @Column(name = "despues", columnDefinition = "TEXT")
    private String afterValue;

    @Column(name = "operacion_id", length = 80)
    private String operationId;

    @Column(name = "ocurrido_en", nullable = false)
    private Instant occurredAt;

    protected EventoPermisoDiagrama() {}

    public EventoPermisoDiagrama(Long proyectoId, Long diagramaId, String actorUserId, String action, String target, String beforeValue, String afterValue, String operationId) {
        this.proyectoId = proyectoId;
        this.diagramaId = diagramaId;
        this.actorUserId = actorUserId;
        this.action = action;
        this.target = target;
        this.beforeValue = beforeValue;
        this.afterValue = afterValue;
        this.operationId = operationId;
    }

    @PrePersist
    void antesDeCrear() {
        occurredAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getProyectoId() { return proyectoId; }
    public Long getDiagramaId() { return diagramaId; }
    public String getActorUserId() { return actorUserId; }
    public String getAction() { return action; }
    public String getTarget() { return target; }
    public String getBeforeValue() { return beforeValue; }
    public String getAfterValue() { return afterValue; }
    public String getOperationId() { return operationId; }
    public Instant getOccurredAt() { return occurredAt; }
}
