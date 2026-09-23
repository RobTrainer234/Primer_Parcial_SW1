package bo.edu.proyecto.caseapp.modelado.presentacion;

import bo.edu.proyecto.caseapp.autenticacion.aplicacion.ActorActual;
import bo.edu.proyecto.caseapp.modelado.aplicacion.ServicioModelado;
import bo.edu.proyecto.caseapp.modelado.dominio.TipoRelacion;
import bo.edu.proyecto.caseapp.modelado.presentacion.RespuestasModelado.RespuestaAtributo;
import bo.edu.proyecto.caseapp.modelado.presentacion.RespuestasModelado.RespuestaEntidad;
import bo.edu.proyecto.caseapp.modelado.presentacion.RespuestasModelado.RespuestaModelo;
import bo.edu.proyecto.caseapp.modelado.presentacion.RespuestasModelado.RespuestaModeloCompleto;
import bo.edu.proyecto.caseapp.modelado.presentacion.RespuestasModelado.RespuestaOperacion;
import bo.edu.proyecto.caseapp.modelado.presentacion.RespuestasModelado.RespuestaRelacion;
import bo.edu.proyecto.caseapp.modelado.presentacion.SolicitudesModelado.SolicitudAtributo;
import bo.edu.proyecto.caseapp.modelado.presentacion.SolicitudesModelado.SolicitudEntidad;
import bo.edu.proyecto.caseapp.modelado.presentacion.SolicitudesModelado.SolicitudModelo;
import bo.edu.proyecto.caseapp.modelado.presentacion.SolicitudesModelado.SolicitudOperacion;
import bo.edu.proyecto.caseapp.modelado.presentacion.SolicitudesModelado.SolicitudRelacion;
import bo.edu.proyecto.caseapp.sincronizacion.aplicacion.ServicioSincronizacion;
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
@RequestMapping
public class ControladorModelado {

    private final ServicioModelado servicioModelado;
    private final ServicioSincronizacion servicioSincronizacion;
    private final ActorActual actorActual;

    public ControladorModelado(ServicioModelado servicioModelado, ServicioSincronizacion servicioSincronizacion, ActorActual actorActual) {
        this.servicioModelado = servicioModelado;
        this.servicioSincronizacion = servicioSincronizacion;
        this.actorActual = actorActual;
    }

    @PostMapping("/proyectos/{proyectoId}/modelos")
    public ResponseEntity<RespuestaModelo> crearModelo(@PathVariable Long proyectoId, @Valid @RequestBody SolicitudModelo solicitud, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        RespuestaModelo respuesta = RespuestaModelo.desde(servicioModelado.crearModeloVisible(proyectoId, ownerUserId, solicitud.nombre()));
        return ResponseEntity.created(URI.create("/modelos/" + respuesta.id())).body(respuesta);
    }

    @GetMapping("/proyectos/{proyectoId}/modelos")
    public List<RespuestaModelo> listarModelos(@PathVariable Long proyectoId, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        return servicioModelado.listarModelosVisibles(proyectoId, ownerUserId).stream().map(RespuestaModelo::desde).toList();
    }

    @GetMapping("/proyectos/{proyectoId}/modelo")
    public RespuestaModeloCompleto obtenerPrimerModeloDelProyecto(@PathVariable Long proyectoId, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        return RespuestaModeloCompleto.desde(servicioModelado.obtenerPrimerModeloPorProyectoVisible(proyectoId, ownerUserId));
    }

    @GetMapping("/modelos/{modeloId}")
    public RespuestaModeloCompleto obtenerModelo(@PathVariable Long modeloId, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        return RespuestaModeloCompleto.desde(servicioModelado.obtenerModeloVisible(modeloId, ownerUserId));
    }

    @PostMapping("/modelos/{modeloId}/entidades")
    public ResponseEntity<RespuestaEntidad> crearEntidad(@PathVariable Long modeloId, @Valid @RequestBody SolicitudEntidad solicitud, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        RespuestaEntidad respuesta = RespuestaEntidad.desde(servicioModelado.crearEntidadVisible(modeloId, ownerUserId, solicitud.nombre(), solicitud.posicionX(), solicitud.posicionY()));
        servicioSincronizacion.publicarCambioModelo(modeloId, "entidad-creada");
        return ResponseEntity.created(URI.create("/entidades/" + respuesta.id())).body(respuesta);
    }

    @PutMapping("/entidades/{entidadId}")
    public RespuestaEntidad actualizarEntidad(@PathVariable Long entidadId, @Valid @RequestBody SolicitudEntidad solicitud, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        var entidad = servicioModelado.actualizarEntidadVisible(entidadId, ownerUserId, solicitud.nombre(), solicitud.posicionX(), solicitud.posicionY());
        servicioSincronizacion.publicarCambioModelo(entidad.getModelo().getId(), "entidad-actualizada");
        return RespuestaEntidad.desde(entidad);
    }

    @DeleteMapping("/entidades/{entidadId}")
    public ResponseEntity<Void> eliminarEntidad(@PathVariable Long entidadId, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        Long modeloId = servicioModelado.obtenerEntidadVisible(entidadId, ownerUserId).getModelo().getId();
        servicioModelado.eliminarEntidadVisible(entidadId, ownerUserId);
        servicioSincronizacion.publicarCambioModelo(modeloId, "entidad-eliminada");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/entidades/{entidadId}/atributos")
    public ResponseEntity<RespuestaAtributo> crearAtributo(@PathVariable Long entidadId, @Valid @RequestBody SolicitudAtributo solicitud, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        var atributo = servicioModelado.crearAtributoVisible(entidadId, ownerUserId, solicitud.nombre(), solicitud.tipoDato(), solicitud.clavePrimaria(), solicitud.obligatorio(), solicitud.valorUnico());
        RespuestaAtributo respuesta = RespuestaAtributo.desde(atributo);
        servicioSincronizacion.publicarCambioModelo(atributo.getEntidad().getModelo().getId(), "atributo-creado");
        return ResponseEntity.created(URI.create("/atributos/" + respuesta.id())).body(respuesta);
    }

    @PutMapping("/atributos/{atributoId}")
    public RespuestaAtributo actualizarAtributo(@PathVariable Long atributoId, @Valid @RequestBody SolicitudAtributo solicitud, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        return RespuestaAtributo.desde(servicioModelado.actualizarAtributoVisible(atributoId, ownerUserId, solicitud.nombre(), solicitud.tipoDato(), solicitud.clavePrimaria(), solicitud.obligatorio(), solicitud.valorUnico()));
    }

    @DeleteMapping("/atributos/{atributoId}")
    public ResponseEntity<Void> eliminarAtributo(@PathVariable Long atributoId, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        servicioModelado.eliminarAtributoVisible(atributoId, ownerUserId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/entidades/{entidadId}/operaciones")
    public ResponseEntity<RespuestaOperacion> crearOperacion(@PathVariable Long entidadId, @Valid @RequestBody SolicitudOperacion solicitud, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        var operacion = servicioModelado.crearOperacionVisible(entidadId, ownerUserId, solicitud.nombre(), solicitud.tipoRetorno(), solicitud.firma(), solicitud.visibilidad());
        RespuestaOperacion respuesta = RespuestaOperacion.desde(operacion);
        servicioSincronizacion.publicarCambioModelo(operacion.getEntidad().getModelo().getId(), "operacion-creada");
        return ResponseEntity.created(URI.create("/operaciones/" + respuesta.id())).body(respuesta);
    }

    @GetMapping("/entidades/{entidadId}/operaciones")
    public List<RespuestaOperacion> listarOperaciones(@PathVariable Long entidadId, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        return servicioModelado.listarOperacionesVisibles(entidadId, ownerUserId).stream().map(RespuestaOperacion::desde).toList();
    }

    @PutMapping("/operaciones/{operacionId}")
    public RespuestaOperacion actualizarOperacion(@PathVariable Long operacionId, @Valid @RequestBody SolicitudOperacion solicitud, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        return RespuestaOperacion.desde(servicioModelado.actualizarOperacionVisible(operacionId, ownerUserId, solicitud.nombre(), solicitud.tipoRetorno(), solicitud.firma(), solicitud.visibilidad()));
    }

    @DeleteMapping("/operaciones/{operacionId}")
    public ResponseEntity<Void> eliminarOperacion(@PathVariable Long operacionId, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        servicioModelado.eliminarOperacionVisible(operacionId, ownerUserId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/modelos/{modeloId}/relaciones")
    public ResponseEntity<RespuestaRelacion> crearRelacion(@PathVariable Long modeloId, @Valid @RequestBody SolicitudRelacion solicitud, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        RespuestaRelacion respuesta = RespuestaRelacion.desde(servicioModelado.crearRelacionVisible(
                modeloId,
                ownerUserId,
                solicitud.entidadOrigenId(),
                solicitud.entidadDestinoId(),
                solicitud.nombre(),
                solicitud.verbo(),
                tipoRelacionOrDefault(solicitud.tipo()),
                solicitud.cardinalidadOrigen(),
                solicitud.cardinalidadDestino()
        ));
        servicioSincronizacion.publicarCambioModelo(modeloId, "relacion-creada");
        return ResponseEntity.created(URI.create("/relaciones/" + respuesta.id())).body(respuesta);
    }

    @PutMapping("/relaciones/{relacionId}")
    public RespuestaRelacion actualizarRelacion(@PathVariable Long relacionId, @Valid @RequestBody SolicitudRelacion solicitud, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        return RespuestaRelacion.desde(servicioModelado.actualizarRelacionVisible(
                relacionId,
                ownerUserId,
                solicitud.entidadOrigenId(),
                solicitud.entidadDestinoId(),
                solicitud.nombre(),
                solicitud.verbo(),
                tipoRelacionOrDefault(solicitud.tipo()),
                solicitud.cardinalidadOrigen(),
                solicitud.cardinalidadDestino()
        ));
    }

    @DeleteMapping("/relaciones/{relacionId}")
    public ResponseEntity<Void> eliminarRelacion(@PathVariable Long relacionId, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        servicioModelado.eliminarRelacionVisible(relacionId, ownerUserId);
        return ResponseEntity.noContent().build();
    }

    private TipoRelacion tipoRelacionOrDefault(TipoRelacion tipo) {
        return tipo != null ? tipo : TipoRelacion.ASOCIACION;
    }
}
