package bo.edu.proyecto.caseapp.modelado.presentacion;

import bo.edu.proyecto.caseapp.modelado.aplicacion.ServicioModelado;
import bo.edu.proyecto.caseapp.modelado.presentacion.RespuestasModelado.RespuestaAtributo;
import bo.edu.proyecto.caseapp.modelado.presentacion.RespuestasModelado.RespuestaEntidad;
import bo.edu.proyecto.caseapp.modelado.presentacion.RespuestasModelado.RespuestaModelo;
import bo.edu.proyecto.caseapp.modelado.presentacion.RespuestasModelado.RespuestaModeloCompleto;
import bo.edu.proyecto.caseapp.modelado.presentacion.RespuestasModelado.RespuestaRelacion;
import bo.edu.proyecto.caseapp.modelado.presentacion.SolicitudesModelado.SolicitudAtributo;
import bo.edu.proyecto.caseapp.modelado.presentacion.SolicitudesModelado.SolicitudEntidad;
import bo.edu.proyecto.caseapp.modelado.presentacion.SolicitudesModelado.SolicitudModelo;
import bo.edu.proyecto.caseapp.modelado.presentacion.SolicitudesModelado.SolicitudRelacion;
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
@RequestMapping
public class ControladorModelado {

    private final ServicioModelado servicioModelado;

    public ControladorModelado(ServicioModelado servicioModelado) {
        this.servicioModelado = servicioModelado;
    }

    @PostMapping("/proyectos/{proyectoId}/modelos")
    public ResponseEntity<RespuestaModelo> crearModelo(@PathVariable Long proyectoId, @Valid @RequestBody SolicitudModelo solicitud) {
        RespuestaModelo respuesta = RespuestaModelo.desde(servicioModelado.crearModelo(proyectoId, solicitud.nombre()));
        return ResponseEntity.created(URI.create("/modelos/" + respuesta.id())).body(respuesta);
    }

    @GetMapping("/proyectos/{proyectoId}/modelos")
    public List<RespuestaModelo> listarModelos(@PathVariable Long proyectoId) {
        return servicioModelado.listarModelos(proyectoId).stream().map(RespuestaModelo::desde).toList();
    }

    @GetMapping("/proyectos/{proyectoId}/modelo")
    public RespuestaModeloCompleto obtenerPrimerModeloDelProyecto(@PathVariable Long proyectoId) {
        return RespuestaModeloCompleto.desde(servicioModelado.obtenerPrimerModeloPorProyecto(proyectoId));
    }

    @GetMapping("/modelos/{modeloId}")
    public RespuestaModeloCompleto obtenerModelo(@PathVariable Long modeloId) {
        return RespuestaModeloCompleto.desde(servicioModelado.obtenerModelo(modeloId));
    }

    @PostMapping("/modelos/{modeloId}/entidades")
    public ResponseEntity<RespuestaEntidad> crearEntidad(@PathVariable Long modeloId, @Valid @RequestBody SolicitudEntidad solicitud) {
        RespuestaEntidad respuesta = RespuestaEntidad.desde(servicioModelado.crearEntidad(modeloId, solicitud.nombre(), solicitud.posicionX(), solicitud.posicionY()));
        return ResponseEntity.created(URI.create("/entidades/" + respuesta.id())).body(respuesta);
    }

    @PutMapping("/entidades/{entidadId}")
    public RespuestaEntidad actualizarEntidad(@PathVariable Long entidadId, @Valid @RequestBody SolicitudEntidad solicitud) {
        return RespuestaEntidad.desde(servicioModelado.actualizarEntidad(entidadId, solicitud.nombre(), solicitud.posicionX(), solicitud.posicionY()));
    }

    @DeleteMapping("/entidades/{entidadId}")
    public ResponseEntity<Void> eliminarEntidad(@PathVariable Long entidadId) {
        servicioModelado.eliminarEntidad(entidadId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/entidades/{entidadId}/atributos")
    public ResponseEntity<RespuestaAtributo> crearAtributo(@PathVariable Long entidadId, @Valid @RequestBody SolicitudAtributo solicitud) {
        RespuestaAtributo respuesta = RespuestaAtributo.desde(servicioModelado.crearAtributo(entidadId, solicitud.nombre(), solicitud.tipoDato(), solicitud.clavePrimaria(), solicitud.obligatorio(), solicitud.valorUnico()));
        return ResponseEntity.created(URI.create("/atributos/" + respuesta.id())).body(respuesta);
    }

    @PutMapping("/atributos/{atributoId}")
    public RespuestaAtributo actualizarAtributo(@PathVariable Long atributoId, @Valid @RequestBody SolicitudAtributo solicitud) {
        return RespuestaAtributo.desde(servicioModelado.actualizarAtributo(atributoId, solicitud.nombre(), solicitud.tipoDato(), solicitud.clavePrimaria(), solicitud.obligatorio(), solicitud.valorUnico()));
    }

    @DeleteMapping("/atributos/{atributoId}")
    public ResponseEntity<Void> eliminarAtributo(@PathVariable Long atributoId) {
        servicioModelado.eliminarAtributo(atributoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/modelos/{modeloId}/relaciones")
    public ResponseEntity<RespuestaRelacion> crearRelacion(@PathVariable Long modeloId, @Valid @RequestBody SolicitudRelacion solicitud) {
        RespuestaRelacion respuesta = RespuestaRelacion.desde(servicioModelado.crearRelacion(
                modeloId,
                solicitud.entidadOrigenId(),
                solicitud.entidadDestinoId(),
                solicitud.nombre(),
                solicitud.cardinalidadOrigen(),
                solicitud.cardinalidadDestino()
        ));
        return ResponseEntity.created(URI.create("/relaciones/" + respuesta.id())).body(respuesta);
    }

    @PutMapping("/relaciones/{relacionId}")
    public RespuestaRelacion actualizarRelacion(@PathVariable Long relacionId, @Valid @RequestBody SolicitudRelacion solicitud) {
        return RespuestaRelacion.desde(servicioModelado.actualizarRelacion(
                relacionId,
                solicitud.entidadOrigenId(),
                solicitud.entidadDestinoId(),
                solicitud.nombre(),
                solicitud.cardinalidadOrigen(),
                solicitud.cardinalidadDestino()
        ));
    }

    @DeleteMapping("/relaciones/{relacionId}")
    public ResponseEntity<Void> eliminarRelacion(@PathVariable Long relacionId) {
        servicioModelado.eliminarRelacion(relacionId);
        return ResponseEntity.noContent().build();
    }
}
