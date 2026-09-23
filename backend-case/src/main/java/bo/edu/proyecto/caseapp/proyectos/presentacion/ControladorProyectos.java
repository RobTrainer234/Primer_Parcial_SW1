package bo.edu.proyecto.caseapp.proyectos.presentacion;

import bo.edu.proyecto.caseapp.autenticacion.aplicacion.ActorActual;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioProyectos;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/proyectos")
public class ControladorProyectos {

    private final ServicioProyectos servicioProyectos;
    private final ActorActual actorActual;

    public ControladorProyectos(ServicioProyectos servicioProyectos, ActorActual actorActual) {
        this.servicioProyectos = servicioProyectos;
        this.actorActual = actorActual;
    }

    @PostMapping
    public ResponseEntity<RespuestaProyecto> crear(@Valid @RequestBody SolicitudProyecto solicitud, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        RespuestaProyecto respuesta = RespuestaProyecto.desde(
                servicioProyectos.crear(solicitud.nombre(), solicitud.descripcion(), ownerUserId)
        );
        return ResponseEntity.created(URI.create("/proyectos/" + respuesta.id())).body(respuesta);
    }

    @GetMapping
    public List<RespuestaProyecto> listar(HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        return servicioProyectos.listarVisibles(ownerUserId).stream().map(RespuestaProyecto::desde).toList();
    }

    @GetMapping("/{id}")
    public RespuestaProyecto obtener(@PathVariable Long id, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        return RespuestaProyecto.desde(servicioProyectos.obtenerVisible(id, ownerUserId));
    }

    @PutMapping("/{id}")
    public RespuestaProyecto actualizar(@PathVariable Long id, @Valid @RequestBody SolicitudProyecto solicitud, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        return RespuestaProyecto.desde(
                servicioProyectos.actualizarVisible(id, ownerUserId, solicitud.nombre(), solicitud.descripcion(), solicitud.estado())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        servicioProyectos.eliminarVisible(id, ownerUserId);
        return ResponseEntity.noContent().build();
    }
}
