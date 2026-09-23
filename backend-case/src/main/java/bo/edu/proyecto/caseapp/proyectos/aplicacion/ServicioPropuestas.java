package bo.edu.proyecto.caseapp.proyectos.aplicacion;

import bo.edu.proyecto.caseapp.compartido.dominio.RecursoNoEncontradoException;
import bo.edu.proyecto.caseapp.compartido.dominio.ReglaNegocioException;
import bo.edu.proyecto.caseapp.configuracion.PropiedadesAiPropuestas;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ClienteQwenPropuestas.ResultadoQwen;
import bo.edu.proyecto.caseapp.proyectos.dominio.Proyecto;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class ServicioPropuestas {
    private final ServicioProyectos servicioProyectos;
    private final PropiedadesAiPropuestas propiedadesAi;
    private final ClienteQwenPropuestas clienteQwen;
    private final AtomicLong secuencia = new AtomicLong(1);
    private final Map<Long, Map<Long, PropuestaModelo>> propuestasPorProyecto = new ConcurrentHashMap<>();

    public ServicioPropuestas(ServicioProyectos servicioProyectos, PropiedadesAiPropuestas propiedadesAi, ClienteQwenPropuestas clienteQwen) {
        this.servicioProyectos = servicioProyectos;
        this.propiedadesAi = propiedadesAi;
        this.clienteQwen = clienteQwen;
    }

    public PropuestaModelo crear(Long projectId, SolicitudPropuesta solicitud) {
        Proyecto proyecto = servicioProyectos.obtener(projectId);
        long id = secuencia.getAndIncrement();
        Instant ahora = Instant.now();
        PropuestaModelo propuesta = new PropuestaModelo(
                id,
                proyecto.getId(),
                texto(solicitud == null ? null : solicitud.title(), "Propuesta de cambio"),
                texto(solicitud == null ? null : solicitud.text(), "Sin texto fuente"),
                texto(solicitud == null ? null : solicitud.source(), "TEXT"),
                texto(solicitud == null ? null : solicitud.sourceStatus(), "READY_FOR_REVIEW"),
                solicitud == null || solicitud.metadata() == null ? Map.of() : solicitud.metadata(),
                "PENDING_REVIEW",
                null,
                "La propuesta es trazable y no modifica el modelo hasta una decision explicita. En esta capa demo aceptar solo marca ACCEPTED; no aplica mutacion automatica.",
                ahora,
                ahora
        );
        propuestasPorProyecto.computeIfAbsent(projectId, ignorado -> new ConcurrentHashMap<>()).put(id, propuesta);
        return propuesta;
    }

    public PropuestaModelo crearQwenTexto(Long projectId, SolicitudPropuestaQwen solicitud) {
        String textoFuente = texto(solicitud == null ? null : solicitud.text(), "Sin texto fuente");
        if (!propiedadesAi.isEnabled()) {
            return crear(projectId, new SolicitudPropuesta(
                    "Propuesta IA textual no habilitada",
                    "AI text proposals are disabled by configuration; no Qwen request was sent.",
                    "AI_QWEN_TEXT",
                    "AI_TEXT_DISABLED",
                    Map.of(
                            "provider", propiedadesAi.getProvider(),
                            "enabled", false,
                            "requestedBy", texto(solicitud == null ? null : solicitud.requestedBy(), "frontend-demo"),
                            "manualFlowUnaffected", true
                    )
            ));
        }
        if (!"qwen".equalsIgnoreCase(texto(propiedadesAi.getProvider(), "deterministic"))) {
            return crear(projectId, new SolicitudPropuesta(
                    "Propuesta IA textual deterministica",
                    "Deterministic provider selected; Qwen was not invoked. Source text: " + textoFuente,
                    "AI_DETERMINISTIC_TEXT",
                    "READY_FOR_REVIEW",
                    Map.of(
                            "provider", propiedadesAi.getProvider(),
                            "enabled", true,
                            "requestedBy", texto(solicitud == null ? null : solicitud.requestedBy(), "frontend-demo"),
                            "qwenInvoked", false
                    )
            ));
        }

        ResultadoQwen resultado = clienteQwen.solicitar(textoFuente, propiedadesAi.getQwen());
        Map<String, Object> metadata = new java.util.LinkedHashMap<>();
        metadata.put("provider", "qwen");
        metadata.put("enabled", true);
        metadata.put("endpoint", propiedadesAi.getQwen().getEndpoint());
        metadata.put("model", propiedadesAi.getQwen().getModel());
        metadata.put("requestedBy", texto(solicitud == null ? null : solicitud.requestedBy(), "frontend-demo"));
        metadata.put("qwenInvoked", true);
        metadata.put("reviewRequired", true);
        if (resultado.statusCode() != null) {
            metadata.put("statusCode", resultado.statusCode());
        }
        if (resultado.error() != null) {
            metadata.put("error", resultado.error());
        }
        return crear(projectId, new SolicitudPropuesta(
                resultado.ok() ? "Propuesta IA textual Qwen" : "Propuesta IA textual no disponible",
                resultado.text(),
                "AI_QWEN_TEXT",
                resultado.sourceStatus(),
                metadata
        ));
    }

    public List<PropuestaModelo> listar(Long projectId) {
        servicioProyectos.obtener(projectId);
        return new ArrayList<>(propuestasPorProyecto.getOrDefault(projectId, Map.of()).values()).stream()
                .sorted(Comparator.comparing(PropuestaModelo::createdAt).reversed())
                .toList();
    }

    public PropuestaModelo obtener(Long projectId, Long proposalId) {
        servicioProyectos.obtener(projectId);
        PropuestaModelo propuesta = propuestasPorProyecto.getOrDefault(projectId, Map.of()).get(proposalId);
        if (propuesta == null) {
            throw new RecursoNoEncontradoException("Propuesta no encontrada: " + proposalId);
        }
        return propuesta;
    }

    public RevisionPropuesta revision(Long projectId, Long proposalId) {
        PropuestaModelo propuesta = obtener(projectId, proposalId);
        return new RevisionPropuesta(
                propuesta.id(),
                propuesta.projectId(),
                propuesta.status(),
                List.of(
                        "Revisar texto fuente y trazabilidad antes de aceptar.",
                        "Aceptar no aplica cambios automaticos al modelo en esta fase demo.",
                        "Rechazar conserva la propuesta para auditoria."
                ),
                propuesta.sourceStatus().equals("READY_FOR_REVIEW") ? "READY" : propuesta.sourceStatus(),
                propuesta.reviewNote()
        );
    }

    public PropuestaModelo decidir(Long projectId, Long proposalId, SolicitudDecision solicitud) {
        PropuestaModelo actual = obtener(projectId, proposalId);
        String decision = texto(solicitud == null ? null : solicitud.decision(), "").toUpperCase();
        if (!decision.equals("ACCEPTED") && !decision.equals("REJECTED")) {
            throw new ReglaNegocioException("Decision invalida: use ACCEPTED o REJECTED");
        }
        PropuestaModelo decidida = new PropuestaModelo(
                actual.id(),
                actual.projectId(),
                actual.title(),
                actual.text(),
                actual.source(),
                actual.sourceStatus(),
                actual.metadata(),
                decision,
                texto(solicitud == null ? null : solicitud.reviewer(), "demo-reviewer"),
                decision.equals("ACCEPTED")
                        ? "Decision aceptada: sin mutacion automatica del modelo; aplicar cambios queda como paso explicito posterior."
                        : "Decision rechazada: modelo sin cambios.",
                actual.createdAt(),
                Instant.now()
        );
        propuestasPorProyecto.get(projectId).put(proposalId, decidida);
        return decidida;
    }

    private static String texto(String valor, String fallback) {
        return valor == null || valor.isBlank() ? fallback : valor.trim();
    }

    public record SolicitudPropuesta(String title, String text, String source, String sourceStatus, Map<String, Object> metadata) {}
    public record SolicitudPropuestaQwen(String text, String requestedBy) {}
    public record SolicitudDecision(String decision, String reviewer, String note) {}
    public record PropuestaModelo(Long id, Long projectId, String title, String text, String source, String sourceStatus, Map<String, Object> metadata, String status, String reviewer, String reviewNote, Instant createdAt, Instant updatedAt) {}
    public record RevisionPropuesta(Long proposalId, Long projectId, String status, List<String> checklist, String sourceStatus, String note) {}
}
