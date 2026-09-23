package bo.edu.proyecto.caseapp.proyectos.presentacion;

import bo.edu.proyecto.caseapp.autenticacion.aplicacion.ActorActual;
import bo.edu.proyecto.caseapp.modelado.aplicacion.ServicioModelado;
import bo.edu.proyecto.caseapp.modelado.dominio.ModeloConceptual;
import bo.edu.proyecto.caseapp.modelado.presentacion.RespuestasModelado.RespuestaModeloCompleto;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.AccesoDiagrama;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.Diagrama;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.EventoPermiso;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.InvitacionProyecto;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.MiembroProyecto;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.RespuestaAccesoDiagrama;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.RespuestaColaboracionDiagrama;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.RespuestaComandoProyecto;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.SolicitudAccesoDiagrama;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.SolicitudAdministradorDiagrama;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.SolicitudCapacidad;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.SolicitudColaboracionDiagrama;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.SolicitudComandoProyecto;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.SolicitudDiagrama;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.SolicitudMiembroProyecto;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioColaboracion.SugerenciaCuenta;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioProyectos;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ControladorProjectsCompatibilidad {
    private final ServicioProyectos servicioProyectos;
    private final ServicioModelado servicioModelado;
    private final ServicioColaboracion servicioColaboracion;
    private final ActorActual actorActual;

    public ControladorProjectsCompatibilidad(ServicioProyectos servicioProyectos, ServicioModelado servicioModelado, ServicioColaboracion servicioColaboracion, ActorActual actorActual) {
        this.servicioProyectos = servicioProyectos;
        this.servicioModelado = servicioModelado;
        this.servicioColaboracion = servicioColaboracion;
        this.actorActual = actorActual;
    }

    @PostMapping
    public ResponseEntity<RespuestaProyecto> crear(@Valid @RequestBody SolicitudProyecto solicitud, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        RespuestaProyecto respuesta = RespuestaProyecto.desde(servicioProyectos.crear(solicitud.nombre(), solicitud.descripcion(), ownerUserId));
        return ResponseEntity.created(URI.create("/projects/" + respuesta.id())).body(respuesta);
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
        return RespuestaProyecto.desde(servicioProyectos.actualizarVisible(id, ownerUserId, solicitud.nombre(), solicitud.descripcion(), solicitud.estado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        servicioProyectos.eliminarVisible(id, ownerUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/model")
    public RespuestaModeloCompleto obtenerModelo(@PathVariable Long id, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        return RespuestaModeloCompleto.desde(servicioModelado.obtenerPrimerModeloPorProyectoVisible(id, ownerUserId));
    }

    @GetMapping("/{id}/versions")
    public List<RespuestaVersionProyecto> versiones(@PathVariable Long id, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        return servicioModelado.listarModelosVisibles(id, ownerUserId).stream().map(RespuestaVersionProyecto::desde).toList();
    }

    @GetMapping("/{projectId}/mobile/descriptor")
    public DescriptorMovil descriptorMovil(@PathVariable Long projectId, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        ModeloConceptual modelo = servicioModelado.obtenerPrimerModeloPorProyectoVisible(projectId, ownerUserId);
        return DescriptorMovil.desde(modelo);
    }

    @GetMapping("/{id}/versions/{revision}")
    public RespuestaModeloCompleto version(@PathVariable Long id, @PathVariable Integer revision, HttpServletRequest request) {
        String ownerUserId = actorActual.requerido(request).getUsuarioId();
        ModeloConceptual modelo = servicioModelado.listarModelosVisibles(id, ownerUserId).stream()
                .filter(candidato -> candidato.getVersion().equals(revision))
                .findFirst()
                .orElseGet(() -> servicioModelado.obtenerPrimerModeloPorProyectoVisible(id, ownerUserId));
        return RespuestaModeloCompleto.desde(modelo);
    }

    @PostMapping("/{projectId}/commands")
    public RespuestaComandoProyecto comandoProyecto(@PathVariable Long projectId, @RequestBody(required = false) SolicitudComandoProyecto solicitud, HttpServletRequest request) {
        String actorId = actorActual.requerido(request).getUsuarioId();
        return servicioColaboracion.registrarComando(projectId, actorId, solicitud);
    }

    @GetMapping("/{projectId}/invitations")
    public List<InvitacionProyecto> invitaciones(@PathVariable Long projectId, HttpServletRequest request) {
        String actorId = actorActual.requerido(request).getUsuarioId();
        return servicioColaboracion.invitaciones(projectId, actorId);
    }

    @GetMapping("/{projectId}/members")
    public List<MiembroProyecto> miembros(@PathVariable Long projectId, HttpServletRequest request) {
        String actorId = actorActual.requerido(request).getUsuarioId();
        return servicioColaboracion.miembros(projectId, actorId);
    }

    @GetMapping("/{projectId}/account-suggestions")
    public List<SugerenciaCuenta> sugerencias(@PathVariable Long projectId, HttpServletRequest request) {
        String actorId = actorActual.requerido(request).getUsuarioId();
        return servicioColaboracion.sugerencias(projectId, actorId);
    }

    @PatchMapping("/{projectId}/members/{userId}")
    public MiembroProyecto actualizarMiembro(@PathVariable Long projectId, @PathVariable String userId, @RequestBody(required = false) SolicitudMiembroProyecto solicitud, HttpServletRequest request) {
        String actorId = actorActual.requerido(request).getUsuarioId();
        return servicioColaboracion.actualizarMiembro(projectId, actorId, userId, solicitud);
    }

    @PutMapping("/{projectId}/members/{userId}/capabilities/create-diagram")
    public MiembroProyecto actualizarCapacidadCrearDiagrama(@PathVariable Long projectId, @PathVariable String userId, @RequestBody(required = false) SolicitudCapacidad solicitud, HttpServletRequest request) {
        String actorId = actorActual.requerido(request).getUsuarioId();
        return servicioColaboracion.actualizarCapacidadCrearDiagrama(projectId, actorId, userId, solicitud);
    }

    @GetMapping({"/{projectId}/permission-history", "/{projectId}/permissions/history"})
    public List<EventoPermiso> historialProyecto(@PathVariable Long projectId, HttpServletRequest request) {
        String actorId = actorActual.requerido(request).getUsuarioId();
        return servicioColaboracion.historialProyecto(projectId, actorId);
    }

    @PostMapping("/{projectId}/diagrams")
    public ResponseEntity<Diagrama> crearDiagrama(@PathVariable Long projectId, @RequestBody(required = false) SolicitudDiagrama solicitud, HttpServletRequest request) {
        String actorId = actorActual.requerido(request).getUsuarioId();
        Diagrama diagrama = servicioColaboracion.crearDiagrama(projectId, actorId, solicitud);
        return ResponseEntity.created(URI.create("/projects/" + projectId + "/diagrams/" + diagrama.id())).body(diagrama);
    }

    @PostMapping("/{projectId}/diagrams/{viewId}/collaboration")
    public RespuestaColaboracionDiagrama iniciarColaboracion(@PathVariable Long projectId, @PathVariable Long viewId, @RequestBody(required = false) SolicitudColaboracionDiagrama solicitud, HttpServletRequest request) {
        String actorId = actorActual.requerido(request).getUsuarioId();
        return servicioColaboracion.iniciarColaboracion(projectId, actorId, viewId, solicitud);
    }

    @PutMapping("/{projectId}/diagrams/{viewId}/collaborators/{userId}")
    public AccesoDiagrama actualizarColaborador(@PathVariable Long projectId, @PathVariable Long viewId, @PathVariable String userId, @RequestBody(required = false) SolicitudAccesoDiagrama solicitud, HttpServletRequest request) {
        String actorId = actorActual.requerido(request).getUsuarioId();
        return servicioColaboracion.actualizarColaborador(projectId, actorId, viewId, userId, solicitud);
    }

    @DeleteMapping("/{projectId}/diagrams/{viewId}/collaborators/{userId}")
    public ResponseEntity<Void> eliminarColaborador(@PathVariable Long projectId, @PathVariable Long viewId, @PathVariable String userId, HttpServletRequest request) {
        String actorId = actorActual.requerido(request).getUsuarioId();
        servicioColaboracion.eliminarColaborador(projectId, actorId, viewId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{projectId}/diagrams/{viewId}/administrator")
    public Diagrama cambiarAdministrador(@PathVariable Long projectId, @PathVariable Long viewId, @RequestBody(required = false) SolicitudAdministradorDiagrama solicitud, HttpServletRequest request) {
        String actorId = actorActual.requerido(request).getUsuarioId();
        return servicioColaboracion.cambiarAdministrador(projectId, actorId, viewId, solicitud);
    }

    @GetMapping("/{projectId}/diagrams/{viewId}/access")
    public RespuestaAccesoDiagrama accesoDiagrama(@PathVariable Long projectId, @PathVariable Long viewId, HttpServletRequest request) {
        String actorId = actorActual.requerido(request).getUsuarioId();
        return servicioColaboracion.accesoDiagrama(projectId, actorId, viewId);
    }

    @GetMapping({"/{projectId}/diagrams/{viewId}/permission-history", "/{projectId}/diagrams/{viewId}/permissions/history"})
    public List<EventoPermiso> historialDiagrama(@PathVariable Long projectId, @PathVariable Long viewId, HttpServletRequest request) {
        String actorId = actorActual.requerido(request).getUsuarioId();
        return servicioColaboracion.historialDiagrama(projectId, actorId, viewId);
    }

    public record DescriptorMovil(Long projectId, Long modelId, Integer revision, List<EntidadMovil> entities, String syncCommandsPath, String conflictHandoffNote, String limitation) {
        static DescriptorMovil desde(ModeloConceptual modelo) {
            return new DescriptorMovil(modelo.getProyecto().getId(), modelo.getId(), modelo.getVersion(), modelo.getEntidades().stream().map(EntidadMovil::desde).toList(), "/modelos/" + modelo.getId() + "/sync/commands", "Si el flush devuelve CONFLICTO, abrir la revision web de conflictos y resolver explicitamente antes de reintentar.", "Descriptor demo derivado del primer modelo del proyecto; la app movil conserva fallback CRUD estatico.");
        }
    }

    public record EntidadMovil(Long id, String name, String resourcePath, List<CampoMovil> fields) {
        static EntidadMovil desde(bo.edu.proyecto.caseapp.modelado.dominio.EntidadModelo entidad) {
            List<CampoMovil> campos = entidad.getAtributos().stream().map(atributo -> new CampoMovil(atributo.getNombre(), atributo.getNombre(), atributo.getTipoDato().name(), atributo.isObligatorio())).toList();
            return new EntidadMovil(entidad.getId(), entidad.getNombre(), entidad.getNombre().toLowerCase(), campos.isEmpty() ? List.of(new CampoMovil("nombre", "nombre", "TEXTO", true)) : campos);
        }
    }

    public record CampoMovil(String name, String label, String type, boolean required) {}

    public record RespuestaVersionProyecto(Long modeloId, Integer revision, String nombre, Instant creadoEn, String notaCompatibilidad) {
        static RespuestaVersionProyecto desde(ModeloConceptual modelo) {
            return new RespuestaVersionProyecto(modelo.getId(), modelo.getVersion(), modelo.getNombre(), Instant.EPOCH, "Capa incremental: la revision actual del modelo se expone como version recuperable.");
        }
    }
}
