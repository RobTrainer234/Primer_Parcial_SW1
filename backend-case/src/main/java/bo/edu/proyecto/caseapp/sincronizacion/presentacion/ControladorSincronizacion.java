package bo.edu.proyecto.caseapp.sincronizacion.presentacion;

import bo.edu.proyecto.caseapp.sincronizacion.aplicacion.ServicioSincronizacion;
import bo.edu.proyecto.caseapp.sincronizacion.aplicacion.ServicioSincronizacion.RespuestaComando;
import bo.edu.proyecto.caseapp.sincronizacion.aplicacion.ServicioSincronizacion.RespuestaSnapshot;
import bo.edu.proyecto.caseapp.sincronizacion.aplicacion.ServicioSincronizacion.SolicitudComando;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/modelos/{modeloId}/sync")
public class ControladorSincronizacion {
    private final ServicioSincronizacion servicioSincronizacion;

    public ControladorSincronizacion(ServicioSincronizacion servicioSincronizacion) {
        this.servicioSincronizacion = servicioSincronizacion;
    }

    @GetMapping("/snapshot")
    public RespuestaSnapshot snapshot(@PathVariable Long modeloId) {
        return servicioSincronizacion.snapshot(modeloId);
    }

    @PostMapping("/commands")
    public RespuestaComando comando(@PathVariable Long modeloId, @RequestBody(required = false) SolicitudComando solicitud) {
        return servicioSincronizacion.registrarComando(modeloId, solicitud);
    }

    @GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter eventos(@PathVariable Long modeloId) {
        return servicioSincronizacion.abrirStream(modeloId);
    }
}
