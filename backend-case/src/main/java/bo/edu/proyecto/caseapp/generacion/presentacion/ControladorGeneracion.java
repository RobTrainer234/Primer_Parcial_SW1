package bo.edu.proyecto.caseapp.generacion.presentacion;

import bo.edu.proyecto.caseapp.artefactos.dominio.ArtefactoGenerado;
import bo.edu.proyecto.caseapp.generacion.aplicacion.ServicioGeneracion;
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

    @PostMapping("/modelos/{modeloId}/generaciones")
    public RespuestaGeneracion generar(@PathVariable Long modeloId) {
        return RespuestaGeneracion.desde(servicioGeneracion.generar(modeloId));
    }

    @GetMapping("/generaciones/{generacionId}")
    public RespuestaGeneracion obtener(@PathVariable Long generacionId) {
        return RespuestaGeneracion.desde(servicioGeneracion.obtenerTrabajo(generacionId));
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
}
