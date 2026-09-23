package bo.edu.proyecto.caseapp.sincronizacion.presentacion;

import bo.edu.proyecto.caseapp.sincronizacion.aplicacion.ServicioSincronizacion;
import bo.edu.proyecto.caseapp.sincronizacion.aplicacion.ServicioSincronizacion.ConflictoSync;
import bo.edu.proyecto.caseapp.sincronizacion.aplicacion.ServicioSincronizacion.Presencia;
import bo.edu.proyecto.caseapp.sincronizacion.aplicacion.ServicioSincronizacion.SolicitudPresencia;
import bo.edu.proyecto.caseapp.sincronizacion.aplicacion.ServicioSincronizacion.SolicitudResolucionConflicto;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class ControladorSincronizacionCompatibilidad {
    private final ServicioSincronizacion servicioSincronizacion;

    public ControladorSincronizacionCompatibilidad(ServicioSincronizacion servicioSincronizacion) {
        this.servicioSincronizacion = servicioSincronizacion;
    }

    @PostMapping("/projects/{projectId}/sync/presence")
    public Presencia presencia(@PathVariable Long projectId, @RequestParam Long modelId, @RequestBody(required = false) SolicitudPresencia solicitud) {
        return servicioSincronizacion.publicarPresencia(projectId, modelId, solicitud);
    }

    @GetMapping("/projects/{projectId}/sync/presence")
    public List<Presencia> listarPresencia(@PathVariable Long projectId, @RequestParam Long modelId) {
        return servicioSincronizacion.listarPresencias(projectId, modelId);
    }

    @GetMapping(path = "/sync/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter eventosGlobales(@RequestParam Long modelId) {
        return servicioSincronizacion.abrirStream(modelId);
    }

    @GetMapping(path = "/projects/{projectId}/sync/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter eventosProyecto(@PathVariable Long projectId, @RequestParam Long modelId) {
        return servicioSincronizacion.abrirStream(modelId);
    }

    @GetMapping("/projects/{projectId}/models/{modelId}/sync/conflicts")
    public List<ConflictoSync> conflictos(@PathVariable Long projectId, @PathVariable Long modelId) {
        return servicioSincronizacion.listarConflictos(projectId, modelId);
    }

    @GetMapping("/projects/{projectId}/models/{modelId}/sync/conflicts/{operationId}")
    public ConflictoSync conflicto(@PathVariable Long projectId, @PathVariable Long modelId, @PathVariable String operationId) {
        return servicioSincronizacion.obtenerConflicto(projectId, modelId, operationId);
    }

    @PostMapping("/projects/{projectId}/models/{modelId}/sync/conflicts/{operationId}/resolve")
    public ConflictoSync resolver(@PathVariable Long projectId, @PathVariable Long modelId, @PathVariable String operationId, @RequestBody(required = false) SolicitudResolucionConflicto solicitud) {
        return servicioSincronizacion.resolverConflicto(projectId, modelId, operationId, solicitud);
    }
}
