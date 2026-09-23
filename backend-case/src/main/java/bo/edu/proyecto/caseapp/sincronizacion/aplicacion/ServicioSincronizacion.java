package bo.edu.proyecto.caseapp.sincronizacion.aplicacion;

import bo.edu.proyecto.caseapp.compartido.dominio.RecursoNoEncontradoException;
import bo.edu.proyecto.caseapp.modelado.aplicacion.ServicioModelado;
import bo.edu.proyecto.caseapp.modelado.dominio.ModeloConceptual;
import bo.edu.proyecto.caseapp.modelado.presentacion.RespuestasModelado.RespuestaModeloCompleto;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class ServicioSincronizacion {
    private final ServicioModelado servicioModelado;
    private final List<SseEmitter> emisores = new CopyOnWriteArrayList<>();
    private final Map<String, Presencia> presencias = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, ConflictoSync>> conflictos = new ConcurrentHashMap<>();
    private final AtomicLong secuenciaConflictos = new AtomicLong(1);

    public ServicioSincronizacion(ServicioModelado servicioModelado) {
        this.servicioModelado = servicioModelado;
    }

    @Transactional(readOnly = true)
    public RespuestaSnapshot snapshot(Long modeloId) {
        ModeloConceptual modelo = servicioModelado.obtenerModelo(modeloId);
        return RespuestaSnapshot.desde(modelo, "snapshot", Instant.now());
    }

    public RespuestaComando registrarComando(Long modeloId, SolicitudComando solicitud) {
        RespuestaSnapshot snapshot = snapshot(modeloId);
        Integer baseRevision = extraerEntero(solicitud == null ? null : solicitud.payload(), "baseRevision");
        if (baseRevision != null && baseRevision < snapshot.revision()) {
            ConflictoSync conflicto = crearConflicto(
                    snapshot.proyectoId(),
                    modeloId,
                    operacionId(solicitud),
                    baseRevision,
                    snapshot.revision(),
                    solicitud == null ? Map.of() : solicitud.payload()
            );
            RespuestaComando respuesta = new RespuestaComando(
                    solicitud == null || solicitud.tipo() == null ? "noop" : solicitud.tipo(),
                    "CONFLICTO",
                    "baseRevision obsoleta; conflicto registrado como " + conflicto.operationId(),
                    snapshot
            );
            publicar("conflict", conflicto);
            return respuesta;
        }
        RespuestaComando respuesta = new RespuestaComando(
                solicitud == null || solicitud.tipo() == null ? "noop" : solicitud.tipo(),
                "ACEPTADO",
                "Comando registrado como evento de sincronizacion en proceso local",
                snapshot
        );
        publicar("command", respuesta);
        return respuesta;
    }

    public Presencia publicarPresencia(Long proyectoId, Long modeloId, SolicitudPresencia solicitud) {
        servicioModelado.obtenerModelo(modeloId);
        String userId = texto(solicitud == null ? null : solicitud.userId(), "demo-user");
        Presencia presencia = new Presencia(
                proyectoId,
                modeloId,
                userId,
                texto(solicitud == null ? null : solicitud.displayName(), userId),
                texto(solicitud == null ? null : solicitud.status(), "online"),
                solicitud == null ? Map.of() : solicitud.cursor(),
                Instant.now()
        );
        presencias.put(proyectoId + ":" + modeloId + ":" + userId, presencia);
        publicar("presence", presencia);
        return presencia;
    }

    public List<Presencia> listarPresencias(Long proyectoId, Long modeloId) {
        return presencias.values().stream()
                .filter(presencia -> presencia.projectId().equals(proyectoId) && presencia.modelId().equals(modeloId))
                .toList();
    }

    public List<ConflictoSync> listarConflictos(Long proyectoId, Long modeloId) {
        return conflictos.getOrDefault(modeloId, Map.of()).values().stream()
                .filter(conflicto -> conflicto.projectId().equals(proyectoId))
                .toList();
    }

    public ConflictoSync obtenerConflicto(Long proyectoId, Long modeloId, String operationId) {
        ConflictoSync conflicto = conflictos.getOrDefault(modeloId, Map.of()).get(operationId);
        if (conflicto == null || !conflicto.projectId().equals(proyectoId)) {
            throw new RecursoNoEncontradoException("Conflicto no encontrado: " + operationId);
        }
        return conflicto;
    }

    public ConflictoSync resolverConflicto(Long proyectoId, Long modeloId, String operationId, SolicitudResolucionConflicto solicitud) {
        ConflictoSync actual = obtenerConflicto(proyectoId, modeloId, operationId);
        ConflictoSync resuelto = new ConflictoSync(
                actual.operationId(),
                actual.projectId(),
                actual.modelId(),
                actual.baseRevision(),
                actual.currentRevision(),
                "RESOLVED",
                actual.detail(),
                actual.commandPayload(),
                texto(solicitud == null ? null : solicitud.resolution(), "accepted-current"),
                Instant.now(),
                actual.createdAt()
        );
        conflictos.computeIfAbsent(modeloId, ignorado -> new ConcurrentHashMap<>()).put(operationId, resuelto);
        publicar("conflict-resolved", resuelto);
        return resuelto;
    }

    public SseEmitter abrirStream(Long modeloId) {
        SseEmitter emisor = new SseEmitter(0L);
        emisores.add(emisor);
        emisor.onCompletion(() -> emisores.remove(emisor));
        emisor.onTimeout(() -> emisores.remove(emisor));
        emisor.onError(error -> emisores.remove(emisor));
        enviar(emisor, "snapshot", snapshot(modeloId));
        return emisor;
    }

    public void publicarCambioModelo(Long modeloId, String tipo) {
        publicar("model-change", Map.of("tipo", tipo, "snapshot", snapshot(modeloId)));
    }

    private ConflictoSync crearConflicto(Long proyectoId, Long modeloId, String operationId, Integer baseRevision, Integer currentRevision, Map<String, Object> payload) {
        ConflictoSync conflicto = new ConflictoSync(
                operationId,
                proyectoId,
                modeloId,
                baseRevision,
                currentRevision,
                "OPEN",
                "Comando rechazado por baseRevision obsoleta en capa demo.",
                payload == null ? Map.of() : payload,
                null,
                null,
                Instant.now()
        );
        conflictos.computeIfAbsent(modeloId, ignorado -> new ConcurrentHashMap<>()).put(operationId, conflicto);
        return conflicto;
    }

    private String operacionId(SolicitudComando solicitud) {
        Object valor = solicitud == null || solicitud.payload() == null ? null : solicitud.payload().get("operationId");
        return valor == null || valor.toString().isBlank() ? "conflict-" + secuenciaConflictos.getAndIncrement() : valor.toString();
    }

    private Integer extraerEntero(Map<String, Object> payload, String clave) {
        if (payload == null || !payload.containsKey(clave)) {
            return null;
        }
        Object valor = payload.get(clave);
        if (valor instanceof Number numero) {
            return numero.intValue();
        }
        try {
            return Integer.parseInt(valor.toString());
        } catch (NumberFormatException error) {
            return null;
        }
    }

    private void publicar(String evento, Object cuerpo) {
        for (SseEmitter emisor : emisores) {
            enviar(emisor, evento, cuerpo);
        }
    }

    private void enviar(SseEmitter emisor, String evento, Object cuerpo) {
        try {
            emisor.send(SseEmitter.event().name(evento).data(cuerpo));
        } catch (IOException | IllegalStateException error) {
            emisores.remove(emisor);
        }
    }

    private static String texto(String valor, String fallback) {
        return valor == null || valor.isBlank() ? fallback : valor;
    }

    public record SolicitudComando(String tipo, Map<String, Object> payload) {}
    public record RespuestaComando(String tipo, String estado, String detalle, RespuestaSnapshot snapshot) {}
    public record SolicitudPresencia(String userId, String displayName, String status, Map<String, Object> cursor) {}
    public record Presencia(Long projectId, Long modelId, String userId, String displayName, String status, Map<String, Object> cursor, Instant seenAt) {}
    public record ConflictoSync(String operationId, Long projectId, Long modelId, Integer baseRevision, Integer currentRevision, String status, String detail, Map<String, Object> commandPayload, String resolution, Instant resolvedAt, Instant createdAt) {}
    public record SolicitudResolucionConflicto(String resolution, Map<String, Object> payload) {}
    public record RespuestaSnapshot(Long modeloId, Long proyectoId, Integer revision, RespuestaModeloCompleto modelo, Map<String, Object> layout, String origen, Instant generadoEn) {
        static RespuestaSnapshot desde(ModeloConceptual modelo, String origen, Instant generadoEn) {
            List<Map<String, Object>> nodos = modelo.getEntidades().stream()
                    .map(entidad -> Map.<String, Object>of(
                            "entidadId", entidad.getId(),
                            "x", entidad.getPosicionX(),
                            "y", entidad.getPosicionY()))
                    .toList();
            return new RespuestaSnapshot(
                    modelo.getId(),
                    modelo.getProyecto().getId(),
                    modelo.getVersion(),
                    RespuestaModeloCompleto.desde(modelo),
                    Map.of("fuente", "posiciones-entidad", "nodos", nodos),
                    origen,
                    generadoEn
            );
        }
    }
}
