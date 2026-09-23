package bo.edu.proyecto.caseapp.generacion.presentacion;

import bo.edu.proyecto.caseapp.artefactos.dominio.ArtefactoGenerado;
import bo.edu.proyecto.caseapp.compartido.dominio.ReglaNegocioException;
import bo.edu.proyecto.caseapp.generacion.aplicacion.ServicioGeneracion;
import bo.edu.proyecto.caseapp.generacion.dominio.EstadoGeneracion;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ControladorGeneracion {

    private final ServicioGeneracion servicioGeneracion;

    public ControladorGeneracion(ServicioGeneracion servicioGeneracion) {
        this.servicioGeneracion = servicioGeneracion;
    }

    @GetMapping("/generation/targets")
    public List<Map<String, String>> targets() {
        return List.of(Map.of("id", "spring-boot", "nombre", "Backend Spring Boot", "estado", "default"));
    }

    @GetMapping("/generation/profiles")
    public List<Map<String, String>> perfiles() {
        return List.of(Map.of("id", "default", "nombre", "Perfil academico por defecto", "target", "spring-boot"));
    }

    @PostMapping("/modelos/{modeloId}/generaciones")
    public RespuestaGeneracion generar(@PathVariable Long modeloId) {
        return enriquecer(servicioGeneracion.generar(modeloId).getId());
    }

    @PostMapping("/generation/runs")
    public RespuestaGeneracion generarRun(@org.springframework.web.bind.annotation.RequestBody SolicitudGeneracion solicitud) {
        if (solicitud == null || solicitud.modeloId() == null) {
            throw new ReglaNegocioException("modeloId es requerido para iniciar generacion");
        }
        return enriquecer(servicioGeneracion.generar(solicitud.modeloId()).getId());
    }

    @GetMapping("/generaciones/{generacionId}")
    public RespuestaGeneracion obtener(@PathVariable Long generacionId) {
        return enriquecer(generacionId);
    }

    @GetMapping("/generation/runs/{generacionId}")
    public RespuestaGeneracion obtenerRun(@PathVariable Long generacionId) {
        return enriquecer(generacionId);
    }

    @GetMapping("/generation/runs/{generacionId}/artifacts")
    public Map<String, Object> artefactos(@PathVariable Long generacionId) {
        ArtefactoGenerado artefacto = servicioGeneracion.obtenerArtefacto(generacionId);
        return Map.of(
                "generacionId", generacionId,
                "nombreArchivo", artefacto.getNombreArchivo(),
                "downloadUrl", "/generaciones/" + generacionId + "/artefacto",
                "sha256", servicioGeneracion.hashArtefacto(generacionId)
        );
    }

    @GetMapping("/generaciones/{generacionId}/artefacto")
    public ResponseEntity<Resource> descargar(@PathVariable Long generacionId) {
        ArtefactoGenerado artefacto = servicioGeneracion.obtenerArtefacto(generacionId);
        Resource recurso = servicioGeneracion.descargarArtefacto(generacionId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + artefacto.getNombreArchivo() + "\"")
                .body(recurso);
    }

    private RespuestaGeneracion enriquecer(Long generacionId) {
        var trabajo = servicioGeneracion.obtenerTrabajo(generacionId);
        String hash = null;
        if (trabajo.getEstado() == EstadoGeneracion.COMPLETADO) {
            try {
                hash = servicioGeneracion.hashArtefacto(generacionId);
            } catch (ReglaNegocioException error) {
                hash = null;
            }
        }
        return RespuestaGeneracion.desde(trabajo, hash);
    }

    public record SolicitudGeneracion(Long modeloId, String target, String perfil) {}
}
