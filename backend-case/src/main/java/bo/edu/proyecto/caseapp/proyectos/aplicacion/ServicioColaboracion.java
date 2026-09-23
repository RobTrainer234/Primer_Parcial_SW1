package bo.edu.proyecto.caseapp.proyectos.aplicacion;

import bo.edu.proyecto.caseapp.autenticacion.dominio.UsuarioAcademico;
import bo.edu.proyecto.caseapp.autenticacion.infraestructura.RepositorioUsuarioAcademico;
import bo.edu.proyecto.caseapp.compartido.dominio.RecursoNoEncontradoException;
import bo.edu.proyecto.caseapp.compartido.dominio.ReglaNegocioException;
import bo.edu.proyecto.caseapp.proyectos.dominio.ColaboradorDiagrama;
import bo.edu.proyecto.caseapp.proyectos.dominio.DiagramaProyecto;
import bo.edu.proyecto.caseapp.proyectos.dominio.EventoPermisoDiagrama;
import bo.edu.proyecto.caseapp.proyectos.dominio.EventoPermisoProyecto;
import bo.edu.proyecto.caseapp.proyectos.dominio.Proyecto;
import bo.edu.proyecto.caseapp.proyectos.infraestructura.RepositorioColaboradorDiagrama;
import bo.edu.proyecto.caseapp.proyectos.infraestructura.RepositorioDiagramaProyecto;
import bo.edu.proyecto.caseapp.proyectos.infraestructura.RepositorioEventoPermisoDiagrama;
import bo.edu.proyecto.caseapp.proyectos.infraestructura.RepositorioEventoPermisoProyecto;
import bo.edu.proyecto.caseapp.proyectos.infraestructura.RepositorioInvitacionProyecto;
import bo.edu.proyecto.caseapp.proyectos.infraestructura.RepositorioMiembroProyecto;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServicioColaboracion {
    private static final String DEMO_ADMIN = "demo-admin";

    private final ServicioProyectos servicioProyectos;
    private final RepositorioMiembroProyecto repositorioMiembros;
    private final RepositorioInvitacionProyecto repositorioInvitaciones;
    private final RepositorioEventoPermisoProyecto repositorioEventos;
    private final RepositorioUsuarioAcademico repositorioUsuarios;
    private final RepositorioDiagramaProyecto repositorioDiagramas;
    private final RepositorioColaboradorDiagrama repositorioColaboradoresDiagrama;
    private final RepositorioEventoPermisoDiagrama repositorioEventosDiagrama;

    public ServicioColaboracion(
            ServicioProyectos servicioProyectos,
            RepositorioMiembroProyecto repositorioMiembros,
            RepositorioInvitacionProyecto repositorioInvitaciones,
            RepositorioEventoPermisoProyecto repositorioEventos,
            RepositorioUsuarioAcademico repositorioUsuarios,
            RepositorioDiagramaProyecto repositorioDiagramas,
            RepositorioColaboradorDiagrama repositorioColaboradoresDiagrama,
            RepositorioEventoPermisoDiagrama repositorioEventosDiagrama
    ) {
        this.servicioProyectos = servicioProyectos;
        this.repositorioMiembros = repositorioMiembros;
        this.repositorioInvitaciones = repositorioInvitaciones;
        this.repositorioEventos = repositorioEventos;
        this.repositorioUsuarios = repositorioUsuarios;
        this.repositorioDiagramas = repositorioDiagramas;
        this.repositorioColaboradoresDiagrama = repositorioColaboradoresDiagrama;
        this.repositorioEventosDiagrama = repositorioEventosDiagrama;
    }

    @Transactional
    public RespuestaComandoProyecto registrarComando(Long proyectoId, String actorId, SolicitudComandoProyecto solicitud) {
        String tipo = texto(solicitud == null ? null : solicitud.tipo(), "NOOP").toUpperCase();
        Object resultado = switch (tipo) {
            case "INVITE_MEMBER" -> invitarMiembro(proyectoId, actorId, solicitud == null ? Map.of() : solicitud.payload());
            case "ACCEPT_INVITATION" -> aceptarInvitacion(proyectoId, actorId, idInvitacion(solicitud));
            case "REJECT_INVITATION" -> rechazarInvitacion(proyectoId, actorId, idInvitacion(solicitud));
            case "REVOKE_INVITATION" -> revocarInvitacion(proyectoId, actorId, idInvitacion(solicitud));
            default -> {
                Proyecto proyecto = proyecto(proyectoId);
                exigirMiembro(proyecto, actorId);
                auditar(proyectoId, actorId, "PROJECT_COMMAND", "project:" + proyectoId, null, "{\"type\":\"" + tipo + "\"}", null);
                yield null;
            }
        };
        return new RespuestaComandoProyecto(tipo, "ACEPTADO", actorId, Instant.now(), resultado == null ? "Comando administrativo registrado." : "Comando aplicado.");
    }

    @Transactional
    public List<InvitacionProyecto> invitaciones(Long proyectoId, String actorId) {
        Proyecto proyecto = proyecto(proyectoId);
        exigirMiembro(proyecto, actorId);
        return repositorioInvitaciones.findByProyectoIdOrderByCreatedAtDesc(proyectoId).stream().map(this::dto).toList();
    }

    @Transactional
    public List<MiembroProyecto> miembros(Long proyectoId, String actorId) {
        Proyecto proyecto = proyecto(proyectoId);
        exigirMiembro(proyecto, actorId);
        return repositorioMiembros.findByProyectoIdOrderByUserIdAsc(proyectoId).stream().map(this::dto).toList();
    }

    @Transactional
    public List<SugerenciaCuenta> sugerencias(Long proyectoId, String actorId) {
        Proyecto proyecto = proyecto(proyectoId);
        exigirAdministrador(proyecto, actorId);
        var miembrosActivos = repositorioMiembros.findByProyectoIdOrderByUserIdAsc(proyectoId).stream()
                .filter(m -> m.getStatus() == bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto.Estado.ACTIVE)
                .map(m -> m.getUserId().toLowerCase())
                .toList();
        return repositorioUsuarios.findAll().stream()
                .filter(UsuarioAcademico::isActivo)
                .filter(u -> !miembrosActivos.contains(u.getUsuarioId().toLowerCase()))
                .filter(u -> !repositorioInvitaciones.existsByProyectoIdAndInviteeUserIdIgnoreCaseAndStatus(proyectoId, u.getUsuarioId(), bo.edu.proyecto.caseapp.proyectos.dominio.InvitacionProyecto.Estado.PENDING))
                .sorted(Comparator.comparing(UsuarioAcademico::getUsuarioId))
                .map(u -> new SugerenciaCuenta(u.getUsuarioId(), u.getNombreVisible(), u.getUsuarioId() + "@case.local", false))
                .toList();
    }

    @Transactional
    public MiembroProyecto actualizarMiembro(Long proyectoId, String actorId, String userId, SolicitudMiembroProyecto solicitud) {
        Proyecto proyecto = proyecto(proyectoId);
        exigirAdministrador(proyecto, actorId);
        var miembro = repositorioMiembros.findByProyectoIdAndUserIdIgnoreCase(proyectoId, userId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Miembro no encontrado: " + userId));
        String antes = serializar(miembro);
        miembro.actualizar(
                solicitud == null ? null : solicitud.displayName(),
                rol(solicitud == null ? null : solicitud.role()),
                solicitud == null ? null : solicitud.canCreateDiagram(),
                estadoMiembro(solicitud == null ? null : solicitud.status())
        );
        auditar(proyectoId, actorId, "PROJECT_MEMBER_UPDATED", "user:" + miembro.getUserId(), antes, serializar(miembro), null);
        return dto(miembro);
    }

    @Transactional
    public MiembroProyecto actualizarCapacidadCrearDiagrama(Long proyectoId, String actorId, String userId, SolicitudCapacidad solicitud) {
        Proyecto proyecto = proyecto(proyectoId);
        exigirAdministrador(proyecto, actorId);
        var miembro = repositorioMiembros.findByProyectoIdAndUserIdIgnoreCase(proyectoId, userId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Miembro no encontrado: " + userId));
        String antes = serializar(miembro);
        miembro.cambiarCapacidadCrearDiagrama(solicitud == null || solicitud.enabled() == null || solicitud.enabled());
        auditar(proyectoId, actorId, "PROJECT_CAPABILITY_UPDATED", "user:" + miembro.getUserId(), antes, serializar(miembro), null);
        return dto(miembro);
    }

    @Transactional
    public List<EventoPermiso> historialProyecto(Long proyectoId, String actorId) {
        Proyecto proyecto = proyecto(proyectoId);
        exigirMiembro(proyecto, actorId);
        return repositorioEventos.findByProyectoIdOrderByOccurredAtDesc(proyectoId).stream().map(this::dto).toList();
    }

    @Transactional
    public Diagrama crearDiagrama(Long proyectoId, String actorId, SolicitudDiagrama solicitud) {
        Proyecto proyecto = proyecto(proyectoId);
        var miembro = exigirMiembro(proyecto, actorId);
        if (!miembro.isCreateDiagram()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenes permiso para crear diagramas en este proyecto");
        }
        String administrador = actorId;
        String administradorSolicitado = solicitud == null ? null : solicitud.administratorUserId();
        if (administradorSolicitado != null && !administradorSolicitado.isBlank() && !administradorSolicitado.equalsIgnoreCase(actorId)) {
            exigirAdministrador(proyecto, actorId);
            administrador = exigirMiembroActivo(proyectoId, administradorSolicitado).getUserId();
        }
        var diagrama = repositorioDiagramas.save(new DiagramaProyecto(proyectoId, texto(solicitud == null ? null : solicitud.name(), "Vista"), administrador));
        guardarColaboradorAdministrador(diagrama.getId(), administrador);
        if (!administrador.equalsIgnoreCase(actorId)) {
            guardarColaboradorAdministrador(diagrama.getId(), actorId);
        }
        auditarDiagrama(proyectoId, diagrama.getId(), actorId, "DIAGRAM_CREATED", "diagram:" + diagrama.getId(), null, serializar(diagrama), null);
        return dto(diagrama);
    }

    @Transactional
    public RespuestaColaboracionDiagrama iniciarColaboracion(Long proyectoId, String actorId, Long viewId, SolicitudColaboracionDiagrama solicitud) {
        Proyecto proyecto = proyecto(proyectoId);
        var diagrama = diagrama(proyectoId, viewId);
        exigirPuedeAdministrarDiagrama(proyecto, diagrama, actorId);
        diagrama.habilitarColaboracion();
        auditarDiagrama(proyectoId, viewId, actorId, "DIAGRAM_COLLABORATION_STARTED", "diagram:" + viewId, null, "Sesion colaborativa abierta", null);
        return new RespuestaColaboracionDiagrama(viewId, actorId, "ACTIVA", Instant.now(), "Compatibilidad academica: estado local no distribuido.");
    }

    @Transactional
    public AccesoDiagrama actualizarColaborador(Long proyectoId, String actorId, Long viewId, String userId, SolicitudAccesoDiagrama solicitud) {
        Proyecto proyecto = proyecto(proyectoId);
        var diagrama = diagrama(proyectoId, viewId);
        exigirPuedeAdministrarDiagrama(proyecto, diagrama, actorId);
        String usuario = exigirMiembroActivo(proyectoId, userId).getUserId();
        var existente = repositorioColaboradoresDiagrama.findByDiagramaIdAndUserIdIgnoreCase(viewId, usuario).orElse(null);
        String antes = existente == null ? null : serializar(existente);
        ColaboradorDiagrama colaborador = existente == null
                ? new ColaboradorDiagrama(viewId, usuario, rolDiagrama(solicitud == null ? null : solicitud.role()), puedeEditar(solicitud), puedeComentar(solicitud))
                : existente;
        if (existente != null) {
            colaborador.actualizar(rolDiagrama(solicitud == null ? null : solicitud.role()), puedeEditar(solicitud), puedeComentar(solicitud));
        }
        repositorioColaboradoresDiagrama.save(colaborador);
        auditarDiagrama(proyectoId, viewId, actorId, "DIAGRAM_COLLABORATOR_UPDATED", "user:" + usuario, antes, serializar(colaborador), null);
        return dto(colaborador);
    }

    @Transactional
    public void eliminarColaborador(Long proyectoId, String actorId, Long viewId, String userId) {
        Proyecto proyecto = proyecto(proyectoId);
        var diagrama = diagrama(proyectoId, viewId);
        exigirPuedeAdministrarDiagrama(proyecto, diagrama, actorId);
        var colaborador = repositorioColaboradoresDiagrama.findByDiagramaIdAndUserIdIgnoreCase(viewId, userId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Colaborador de diagrama no encontrado: " + userId));
        if (diagrama.getAdministratorUserId().equalsIgnoreCase(colaborador.getUserId()) || (colaborador.administrador() && administradores(viewId).size() <= 1)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar el ultimo administrador del diagrama");
        }
        String antes = serializar(colaborador);
        repositorioColaboradoresDiagrama.delete(colaborador);
        auditarDiagrama(proyectoId, viewId, actorId, "DIAGRAM_COLLABORATOR_REMOVED", "user:" + colaborador.getUserId(), antes, null, null);
    }

    @Transactional
    public Diagrama cambiarAdministrador(Long proyectoId, String actorId, Long viewId, SolicitudAdministradorDiagrama solicitud) {
        Proyecto proyecto = proyecto(proyectoId);
        var diagrama = diagrama(proyectoId, viewId);
        exigirPuedeAdministrarDiagrama(proyecto, diagrama, actorId);
        String administrador = texto(solicitud == null ? null : solicitud.userId(), diagrama.getAdministratorUserId());
        administrador = exigirMiembroActivo(proyectoId, administrador).getUserId();
        String antes = serializar(diagrama);
        diagrama.cambiarAdministrador(administrador);
        guardarColaboradorAdministrador(viewId, administrador);
        auditarDiagrama(proyectoId, viewId, actorId, "DIAGRAM_ADMIN_CHANGED", "diagram:" + viewId, antes, serializar(diagrama), null);
        return dto(diagrama);
    }

    @Transactional
    public RespuestaAccesoDiagrama accesoDiagrama(Long proyectoId, String actorId, Long viewId) {
        Proyecto proyecto = proyecto(proyectoId);
        exigirMiembro(proyecto, actorId);
        DiagramaProyecto diagrama = diagrama(proyectoId, viewId);
        List<AccesoDiagrama> accesos = repositorioColaboradoresDiagrama.findByDiagramaIdOrderByUserIdAsc(viewId).stream().map(this::dto).toList();
        return new RespuestaAccesoDiagrama(dto(diagrama), accesos);
    }

    @Transactional
    public List<EventoPermiso> historialDiagrama(Long proyectoId, String actorId, Long viewId) {
        Proyecto proyecto = proyecto(proyectoId);
        exigirMiembro(proyecto, actorId);
        diagrama(proyectoId, viewId);
        return repositorioEventosDiagrama.findByProyectoIdAndDiagramaIdOrderByOccurredAtDesc(proyectoId, viewId).stream().map(this::dto).toList();
    }

    private InvitacionProyecto invitarMiembro(Long proyectoId, String actorId, Map<String, Object> payload) {
        Proyecto proyecto = proyecto(proyectoId);
        exigirAdministrador(proyecto, actorId);
        String invitee = valor(payload, "userId", "inviteeUserId");
        UsuarioAcademico usuario = usuarioActivo(invitee);
        if (repositorioMiembros.existsByProyectoIdAndUserIdIgnoreCaseAndStatus(proyectoId, usuario.getUsuarioId(), bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto.Estado.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya es miembro activo del proyecto");
        }
        if (repositorioInvitaciones.existsByProyectoIdAndInviteeUserIdIgnoreCaseAndStatus(proyectoId, usuario.getUsuarioId(), bo.edu.proyecto.caseapp.proyectos.dominio.InvitacionProyecto.Estado.PENDING)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una invitacion pendiente para el usuario");
        }
        var invitacion = new bo.edu.proyecto.caseapp.proyectos.dominio.InvitacionProyecto(proyectoId, usuario.getUsuarioId(), actorId, rol(valor(payload, "role")), UUID.randomUUID().toString(), Instant.now().plus(7, ChronoUnit.DAYS));
        repositorioInvitaciones.save(invitacion);
        auditar(proyectoId, actorId, "INVITATION_CREATED", "user:" + usuario.getUsuarioId(), null, serializar(invitacion), null);
        return dto(invitacion);
    }

    private InvitacionProyecto aceptarInvitacion(Long proyectoId, String actorId, Long invitacionId) {
        var invitacion = invitacion(proyectoId, invitacionId);
        if (!invitacion.getInviteeUserId().equalsIgnoreCase(actorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el invitado puede aceptar la invitacion");
        }
        validarPendiente(invitacion);
        String antes = serializar(invitacion);
        invitacion.aceptar();
        UsuarioAcademico usuario = usuarioActivo(actorId);
        var miembro = repositorioMiembros.findByProyectoIdAndUserIdIgnoreCase(proyectoId, actorId)
                .orElseGet(() -> new bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto(proyectoId, usuario.getUsuarioId(), usuario.getNombreVisible(), invitacion.getRole()));
        miembro.actualizar(usuario.getNombreVisible(), invitacion.getRole(), null, bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto.Estado.ACTIVE);
        repositorioMiembros.save(miembro);
        auditar(proyectoId, actorId, "INVITATION_ACCEPTED", "invitation:" + invitacion.getId(), antes, serializar(invitacion), null);
        return dto(invitacion);
    }

    private InvitacionProyecto rechazarInvitacion(Long proyectoId, String actorId, Long invitacionId) {
        var invitacion = invitacion(proyectoId, invitacionId);
        if (!invitacion.getInviteeUserId().equalsIgnoreCase(actorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el invitado puede rechazar la invitacion");
        }
        validarPendiente(invitacion);
        String antes = serializar(invitacion);
        invitacion.rechazar();
        auditar(proyectoId, actorId, "INVITATION_REJECTED", "invitation:" + invitacion.getId(), antes, serializar(invitacion), null);
        return dto(invitacion);
    }

    private InvitacionProyecto revocarInvitacion(Long proyectoId, String actorId, Long invitacionId) {
        Proyecto proyecto = proyecto(proyectoId);
        exigirAdministrador(proyecto, actorId);
        var invitacion = invitacion(proyectoId, invitacionId);
        validarPendiente(invitacion);
        String antes = serializar(invitacion);
        invitacion.revocar();
        auditar(proyectoId, actorId, "INVITATION_REVOKED", "invitation:" + invitacion.getId(), antes, serializar(invitacion), null);
        return dto(invitacion);
    }

    private Proyecto proyecto(Long proyectoId) {
        Proyecto proyecto = servicioProyectos.obtener(proyectoId);
        asegurarOwner(proyecto, DEMO_ADMIN);
        asegurarOwner(proyecto, proyecto.getOwnerUserId());
        return proyecto;
    }

    private void asegurarOwner(Proyecto proyecto, String userId) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        repositorioMiembros.findByProyectoIdAndUserIdIgnoreCase(proyecto.getId(), userId).orElseGet(() -> repositorioUsuarios.findByUsuarioIdIgnoreCase(userId)
                .map(u -> repositorioMiembros.save(new bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto(proyecto.getId(), u.getUsuarioId(), u.getNombreVisible(), bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto.Rol.OWNER)))
                .orElse(null));
    }

    private bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto exigirMiembro(Proyecto proyecto, String actorId) {
        if (proyecto.getOwnerUserId().equalsIgnoreCase(actorId)) {
            return repositorioMiembros.findByProyectoIdAndUserIdIgnoreCase(proyecto.getId(), actorId)
                    .orElseGet(() -> new bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto(proyecto.getId(), actorId, actorId, bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto.Rol.OWNER));
        }
        return repositorioMiembros.findByProyectoIdAndUserIdIgnoreCase(proyecto.getId(), actorId)
                .filter(bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto::activo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenes acceso a este proyecto"));
    }

    private bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto exigirAdministrador(Proyecto proyecto, String actorId) {
        var miembro = exigirMiembro(proyecto, actorId);
        if (!miembro.administrador()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La operacion requiere rol OWNER o ADMIN");
        }
        return miembro;
    }

    private void exigirPuedeAdministrarDiagrama(Proyecto proyecto, DiagramaProyecto diagrama, String actorId) {
        var miembro = exigirMiembro(proyecto, actorId);
        if (miembro.administrador() || diagrama.getAdministratorUserId().equalsIgnoreCase(actorId) || repositorioColaboradoresDiagrama.findByDiagramaIdAndUserIdIgnoreCase(diagrama.getId(), actorId).filter(ColaboradorDiagrama::administrador).isPresent()) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La operacion requiere administrador de proyecto o de diagrama");
    }

    private bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto exigirMiembroActivo(Long proyectoId, String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ReglaNegocioException("El usuario es obligatorio");
        }
        usuarioActivo(userId);
        return repositorioMiembros.findByProyectoIdAndUserIdIgnoreCase(proyectoId, userId)
                .filter(bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto::activo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "El usuario debe ser miembro activo del proyecto"));
    }

    private void guardarColaboradorAdministrador(Long diagramaId, String userId) {
        var colaborador = repositorioColaboradoresDiagrama.findByDiagramaIdAndUserIdIgnoreCase(diagramaId, userId)
                .orElseGet(() -> new ColaboradorDiagrama(diagramaId, userId, "ADMIN", true, true));
        colaborador.actualizar("ADMIN", true, true);
        repositorioColaboradoresDiagrama.save(colaborador);
    }

    private List<ColaboradorDiagrama> administradores(Long diagramaId) {
        return repositorioColaboradoresDiagrama.findByDiagramaIdOrderByUserIdAsc(diagramaId).stream().filter(ColaboradorDiagrama::administrador).toList();
    }

    private UsuarioAcademico usuarioActivo(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ReglaNegocioException("El usuario invitado es obligatorio");
        }
        return repositorioUsuarios.findByUsuarioIdIgnoreCase(userId.trim())
                .filter(UsuarioAcademico::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario academico activo no encontrado: " + userId));
    }

    private bo.edu.proyecto.caseapp.proyectos.dominio.InvitacionProyecto invitacion(Long proyectoId, Long id) {
        if (id == null) {
            throw new ReglaNegocioException("La invitacion es obligatoria");
        }
        return repositorioInvitaciones.findByProyectoIdAndId(proyectoId, id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Invitacion no encontrada: " + id));
    }

    private void validarPendiente(bo.edu.proyecto.caseapp.proyectos.dominio.InvitacionProyecto invitacion) {
        if (!invitacion.pendienteVigente(Instant.now())) {
            if (invitacion.getStatus() == bo.edu.proyecto.caseapp.proyectos.dominio.InvitacionProyecto.Estado.PENDING) {
                invitacion.expirar();
            }
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "La invitacion no esta pendiente o vigente");
        }
    }

    private DiagramaProyecto diagrama(Long proyectoId, Long viewId) {
        if (viewId == null) {
            throw new ReglaNegocioException("El diagrama/vista es obligatorio");
        }
        return repositorioDiagramas.findByProyectoIdAndIdAndEstado(proyectoId, viewId, DiagramaProyecto.Estado.ACTIVE)
                .orElseThrow(() -> new RecursoNoEncontradoException("Diagrama/vista no encontrado: " + viewId));
    }

    private void auditar(Long proyectoId, String actor, String accion, String target, String antes, String despues, String operacionId) {
        repositorioEventos.save(new EventoPermisoProyecto(proyectoId, actor, accion, target, antes, despues, operacionId));
    }

    private void auditarDiagrama(Long proyectoId, Long diagramaId, String actor, String accion, String target, String antes, String despues, String operacionId) {
        repositorioEventosDiagrama.save(new EventoPermisoDiagrama(proyectoId, diagramaId, actor, accion, target, antes, despues, operacionId));
    }

    private bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto.Rol rol(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto.Rol.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ReglaNegocioException("Rol de proyecto invalido: " + valor);
        }
    }

    private bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto.Estado estadoMiembro(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto.Estado.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ReglaNegocioException("Estado de miembro invalido: " + valor);
        }
    }

    private String rolDiagrama(String valor) {
        String rol = texto(valor, "EDITOR").toUpperCase();
        if (!List.of("ADMIN", "OWNER", "EDITOR", "VIEWER", "COMMENTER").contains(rol)) {
            throw new ReglaNegocioException("Rol de diagrama invalido: " + valor);
        }
        return rol;
    }

    private boolean puedeEditar(SolicitudAccesoDiagrama solicitud) {
        return solicitud == null || solicitud.canEdit() == null || solicitud.canEdit();
    }

    private boolean puedeComentar(SolicitudAccesoDiagrama solicitud) {
        return solicitud == null || solicitud.canComment() == null || solicitud.canComment();
    }

    private Long idInvitacion(SolicitudComandoProyecto solicitud) {
        if (solicitud == null || solicitud.payload() == null) {
            return null;
        }
        Object valor = solicitud.payload().getOrDefault("invitationId", solicitud.payload().get("id"));
        if (valor instanceof Number numero) {
            return numero.longValue();
        }
        if (valor instanceof String texto && !texto.isBlank()) {
            return Long.parseLong(texto);
        }
        return null;
    }

    private String valor(Map<String, Object> payload, String... nombres) {
        if (payload == null) {
            return null;
        }
        for (String nombre : nombres) {
            Object valor = payload.get(nombre);
            if (valor != null) {
                return valor.toString();
            }
        }
        return null;
    }

    private String serializar(bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto miembro) {
        return "{\"userId\":\"" + miembro.getUserId() + "\",\"role\":\"" + miembro.getRole() + "\",\"status\":\"" + miembro.getStatus() + "\",\"createDiagram\":" + miembro.isCreateDiagram() + "}";
    }

    private String serializar(bo.edu.proyecto.caseapp.proyectos.dominio.InvitacionProyecto invitacion) {
        return "{\"id\":" + invitacion.getId() + ",\"inviteeUserId\":\"" + invitacion.getInviteeUserId() + "\",\"role\":\"" + invitacion.getRole() + "\",\"status\":\"" + invitacion.getStatus() + "\"}";
    }

    private String serializar(DiagramaProyecto diagrama) {
        return "{\"id\":" + diagrama.getId() + ",\"projectId\":" + diagrama.getProyectoId() + ",\"name\":\"" + diagrama.getNombre() + "\",\"administratorUserId\":\"" + diagrama.getAdministratorUserId() + "\",\"collaborationEnabled\":" + diagrama.isCollaborationEnabled() + ",\"status\":\"" + diagrama.getEstado() + "\"}";
    }

    private String serializar(ColaboradorDiagrama colaborador) {
        return "{\"userId\":\"" + colaborador.getUserId() + "\",\"role\":\"" + colaborador.getRole() + "\",\"canEdit\":" + colaborador.isCanEdit() + ",\"canComment\":" + colaborador.isCanComment() + "}";
    }

    private InvitacionProyecto dto(bo.edu.proyecto.caseapp.proyectos.dominio.InvitacionProyecto invitacion) {
        return new InvitacionProyecto(String.valueOf(invitacion.getId()), invitacion.getInviteeUserId() + "@case.local", invitacion.getRole().name(), invitacion.getStatus().name(), invitacion.getCreatedAt(), invitacion.getInviteeUserId(), invitacion.getInviterUserId(), invitacion.getToken(), invitacion.getExpiresAt());
    }

    private MiembroProyecto dto(bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto miembro) {
        return new MiembroProyecto(miembro.getUserId(), miembro.getDisplayName(), miembro.getRole().name(), miembro.isCreateDiagram(), miembro.activo(), miembro.getUpdatedAt(), miembro.getStatus().name(), miembro.isEditModel(), miembro.isManageMembers(), miembro.isManagePermissions());
    }

    private EventoPermiso dto(EventoPermisoProyecto evento) {
        Long viewId = null;
        if (evento.getTarget() != null && evento.getTarget().startsWith("diagram:")) {
            viewId = Long.parseLong(evento.getTarget().substring("diagram:".length()));
        }
        String detalle = evento.getAfterValue() == null ? evento.getBeforeValue() : evento.getAfterValue();
        return new EventoPermiso(evento.getId(), evento.getAction(), evento.getActorUserId(), viewId, detalle, evento.getOccurredAt(), evento.getTarget(), evento.getBeforeValue(), evento.getAfterValue(), evento.getOperationId());
    }

    private EventoPermiso dto(EventoPermisoDiagrama evento) {
        String detalle = evento.getAfterValue() == null ? evento.getBeforeValue() : evento.getAfterValue();
        return new EventoPermiso(evento.getId(), evento.getAction(), evento.getActorUserId(), evento.getDiagramaId(), detalle, evento.getOccurredAt(), evento.getTarget(), evento.getBeforeValue(), evento.getAfterValue(), evento.getOperationId());
    }

    private Diagrama dto(DiagramaProyecto diagrama) {
        return new Diagrama(diagrama.getId(), diagrama.getProyectoId(), diagrama.getNombre(), diagrama.getAdministratorUserId(), diagrama.getCreatedAt());
    }

    private AccesoDiagrama dto(ColaboradorDiagrama colaborador) {
        return new AccesoDiagrama(colaborador.getUserId(), colaborador.getRole(), colaborador.isCanEdit(), colaborador.isCanComment(), colaborador.getUpdatedAt());
    }

    private static String texto(String valor, String fallback) {
        return valor == null || valor.isBlank() ? fallback : valor.trim();
    }

    public record SolicitudComandoProyecto(String tipo, String actor, Map<String, Object> payload) {}
    public record RespuestaComandoProyecto(String tipo, String estado, String actor, Instant registradoEn, String detalle) {}
    public record InvitacionProyecto(String id, String email, String role, String status, Instant createdAt, String userId, String inviterUserId, String token, Instant expiresAt) {}
    public record MiembroProyecto(String userId, String displayName, String role, boolean canCreateDiagram, boolean active, Instant updatedAt, String status, boolean canEditModel, boolean canManageMembers, boolean canManagePermissions) {}
    public record SugerenciaCuenta(String userId, String displayName, String email, boolean alreadyMember) {}
    public record SolicitudMiembroProyecto(String displayName, String role, Boolean canCreateDiagram, String status) {}
    public record SolicitudCapacidad(Boolean enabled) {}
    public record EventoPermiso(Long id, String type, String actor, Long viewId, String detail, Instant occurredAt, String target, String before, String after, String operationId) {}
    public record SolicitudDiagrama(String name, String administratorUserId) {}
    public record Diagrama(Long id, Long projectId, String name, String administratorUserId, Instant createdAt) {}
    public record SolicitudColaboracionDiagrama(String userId) {}
    public record RespuestaColaboracionDiagrama(Long viewId, String userId, String status, Instant startedAt, String limitation) {}
    public record SolicitudAccesoDiagrama(String role, Boolean canEdit, Boolean canComment) {}
    public record AccesoDiagrama(String userId, String role, boolean canEdit, boolean canComment, Instant updatedAt) {}
    public record SolicitudAdministradorDiagrama(String userId) {}
    public record RespuestaAccesoDiagrama(Diagrama diagram, List<AccesoDiagrama> collaborators) {}
}
