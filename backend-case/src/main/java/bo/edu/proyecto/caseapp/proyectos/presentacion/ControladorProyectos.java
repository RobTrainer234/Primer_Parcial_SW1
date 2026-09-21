package bo.edu.proyecto.caseapp.proyectos.presentacion;

import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioProyectos;
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

    public ControladorProyectos(ServicioProyectos servicioProyectos) {
        this.servicioProyectos = servicioProyectos;
    }

    @PostMapping
    public ResponseEntity<RespuestaProyecto> crear(@Valid @RequestBody SolicitudProyecto solicitud) {
        RespuestaProyecto respuesta = RespuestaProyecto.desde(
                servicioProyectos.crear(solicitud.nombre(), solicitud.descripcion())
        );
        return ResponseEntity.created(URI.create("/proyectos/" + respuesta.id())).body(respuesta);
    }

    @GetMapping
    public List<RespuestaProyecto> listar() {
        return servicioProyectos.listar().stream().map(RespuestaProyecto::desde).toList();
    }

    @GetMapping("/{id}")
    public RespuestaProyecto obtener(@PathVariable Long id) {
        return RespuestaProyecto.desde(servicioProyectos.obtener(id));
    }

    @PutMapping("/{id}")
    public RespuestaProyecto actualizar(@PathVariable Long id, @Valid @RequestBody SolicitudProyecto solicitud) {
        return RespuestaProyecto.desde(
                servicioProyectos.actualizar(id, solicitud.nombre(), solicitud.descripcion(), solicitud.estado())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicioProyectos.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
