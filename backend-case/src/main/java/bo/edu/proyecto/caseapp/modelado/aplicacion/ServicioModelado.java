package bo.edu.proyecto.caseapp.modelado.aplicacion;

import bo.edu.proyecto.caseapp.compartido.dominio.RecursoNoEncontradoException;
import bo.edu.proyecto.caseapp.compartido.dominio.ReglaNegocioException;
import bo.edu.proyecto.caseapp.modelado.dominio.AtributoEntidad;
import bo.edu.proyecto.caseapp.modelado.dominio.Cardinalidad;
import bo.edu.proyecto.caseapp.modelado.dominio.EntidadModelo;
import bo.edu.proyecto.caseapp.modelado.dominio.ModeloConceptual;
import bo.edu.proyecto.caseapp.modelado.dominio.OperacionEntidad;
import bo.edu.proyecto.caseapp.modelado.dominio.RelacionModelo;
import bo.edu.proyecto.caseapp.modelado.dominio.TipoDato;
import bo.edu.proyecto.caseapp.modelado.dominio.TipoRelacion;
import bo.edu.proyecto.caseapp.modelado.infraestructura.RepositorioAtributoEntidad;
import bo.edu.proyecto.caseapp.modelado.infraestructura.RepositorioEntidadModelo;
import bo.edu.proyecto.caseapp.modelado.infraestructura.RepositorioModeloConceptual;
import bo.edu.proyecto.caseapp.modelado.infraestructura.RepositorioOperacionEntidad;
import bo.edu.proyecto.caseapp.modelado.infraestructura.RepositorioRelacionModelo;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioProyectos;
import bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto;
import bo.edu.proyecto.caseapp.proyectos.dominio.Proyecto;
import bo.edu.proyecto.caseapp.proyectos.infraestructura.RepositorioMiembroProyecto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServicioModelado {

    private final ServicioProyectos servicioProyectos;
    private final RepositorioModeloConceptual repositorioModelo;
    private final RepositorioEntidadModelo repositorioEntidad;
    private final RepositorioAtributoEntidad repositorioAtributo;
    private final RepositorioRelacionModelo repositorioRelacion;
    private final RepositorioOperacionEntidad repositorioOperacion;
    private final RepositorioMiembroProyecto repositorioMiembros;

    public ServicioModelado(ServicioProyectos servicioProyectos, RepositorioModeloConceptual repositorioModelo, RepositorioEntidadModelo repositorioEntidad, RepositorioAtributoEntidad repositorioAtributo, RepositorioRelacionModelo repositorioRelacion, RepositorioOperacionEntidad repositorioOperacion, RepositorioMiembroProyecto repositorioMiembros) {
        this.servicioProyectos = servicioProyectos;
        this.repositorioModelo = repositorioModelo;
        this.repositorioEntidad = repositorioEntidad;
        this.repositorioAtributo = repositorioAtributo;
        this.repositorioRelacion = repositorioRelacion;
        this.repositorioOperacion = repositorioOperacion;
        this.repositorioMiembros = repositorioMiembros;
    }

    @Transactional
    public ModeloConceptual crearModelo(Long proyectoId, String nombre) {
        Proyecto proyecto = servicioProyectos.obtener(proyectoId);
        return repositorioModelo.save(new ModeloConceptual(proyecto, nombre));
    }

    @Transactional
    public ModeloConceptual crearModeloVisible(Long proyectoId, String ownerUserId, String nombre) {
        Proyecto proyecto = obtenerProyectoVisible(proyectoId, ownerUserId);
        exigirPuedeEditarProyecto(proyecto, ownerUserId);
        return repositorioModelo.save(new ModeloConceptual(proyecto, nombre));
    }

    @Transactional(readOnly = true)
    public List<ModeloConceptual> listarModelos(Long proyectoId) {
        servicioProyectos.obtener(proyectoId);
        return repositorioModelo.findByProyectoId(proyectoId);
    }

    @Transactional(readOnly = true)
    public List<ModeloConceptual> listarModelosVisibles(Long proyectoId, String ownerUserId) {
        obtenerProyectoVisible(proyectoId, ownerUserId);
        return repositorioModelo.findByProyectoId(proyectoId);
    }

    @Transactional(readOnly = true)
    public ModeloConceptual obtenerModelo(Long modeloId) {
        return repositorioModelo.findById(modeloId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Modelo no encontrado: " + modeloId));
    }

    @Transactional(readOnly = true)
    public ModeloConceptual obtenerModeloVisible(Long modeloId, String ownerUserId) {
        ModeloConceptual modelo = obtenerModelo(modeloId);
        exigirProyectoVisible(modelo, ownerUserId);
        modelo.getEntidades().forEach(entidad -> {
            entidad.getAtributos().size();
            entidad.getOperaciones().size();
        });
        modelo.getRelaciones().size();
        return modelo;
    }

    @Transactional(readOnly = true)
    public ModeloConceptual obtenerPrimerModeloPorProyecto(Long proyectoId) {
        servicioProyectos.obtener(proyectoId);
        return repositorioModelo.findFirstByProyectoIdOrderByIdAsc(proyectoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto no tiene modelo conceptual"));
    }

    @Transactional(readOnly = true)
    public ModeloConceptual obtenerPrimerModeloPorProyectoVisible(Long proyectoId, String ownerUserId) {
        obtenerProyectoVisible(proyectoId, ownerUserId);
        ModeloConceptual modelo = repositorioModelo.findFirstByProyectoIdOrderByIdAsc(proyectoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto no tiene modelo conceptual"));
        modelo.getEntidades().forEach(entidad -> {
            entidad.getAtributos().size();
            entidad.getOperaciones().size();
        });
        modelo.getRelaciones().size();
        return modelo;
    }

    @Transactional
    public EntidadModelo crearEntidad(Long modeloId, String nombre, Integer posicionX, Integer posicionY) {
        ModeloConceptual modelo = obtenerModelo(modeloId);
        EntidadModelo entidad = new EntidadModelo(modelo, nombre, posicionX, posicionY);
        return repositorioEntidad.save(entidad);
    }

    @Transactional
    public EntidadModelo crearEntidadVisible(Long modeloId, String ownerUserId, String nombre, Integer posicionX, Integer posicionY) {
        ModeloConceptual modelo = obtenerModelo(modeloId);
        exigirPuedeEditarModelo(modelo, ownerUserId);
        EntidadModelo entidad = new EntidadModelo(modelo, nombre, posicionX, posicionY);
        return repositorioEntidad.save(entidad);
    }

    @Transactional
    public EntidadModelo actualizarEntidad(Long entidadId, String nombre, Integer posicionX, Integer posicionY) {
        EntidadModelo entidad = obtenerEntidad(entidadId);
        entidad.actualizar(nombre, posicionX, posicionY);
        return entidad;
    }

    @Transactional
    public EntidadModelo actualizarEntidadVisible(Long entidadId, String ownerUserId, String nombre, Integer posicionX, Integer posicionY) {
        EntidadModelo entidad = obtenerEntidad(entidadId);
        exigirPuedeEditarModelo(entidad.getModelo(), ownerUserId);
        entidad.actualizar(nombre, posicionX, posicionY);
        return entidad;
    }

    @Transactional(readOnly = true)
    public EntidadModelo obtenerEntidad(Long entidadId) {
        return repositorioEntidad.findById(entidadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entidad no encontrada: " + entidadId));
    }

    @Transactional(readOnly = true)
    public EntidadModelo obtenerEntidadVisible(Long entidadId, String ownerUserId) {
        EntidadModelo entidad = obtenerEntidad(entidadId);
        exigirProyectoVisible(entidad.getModelo(), ownerUserId);
        entidad.getAtributos().size();
        entidad.getOperaciones().size();
        return entidad;
    }

    @Transactional
    public void eliminarEntidad(Long entidadId) {
        repositorioEntidad.delete(obtenerEntidad(entidadId));
    }

    @Transactional
    public void eliminarEntidadVisible(Long entidadId, String ownerUserId) {
        EntidadModelo entidad = obtenerEntidad(entidadId);
        exigirPuedeEditarModelo(entidad.getModelo(), ownerUserId);
        repositorioEntidad.delete(entidad);
    }

    @Transactional
    public AtributoEntidad crearAtributo(Long entidadId, String nombre, TipoDato tipoDato, boolean clavePrimaria, boolean obligatorio, boolean valorUnico) {
        EntidadModelo entidad = obtenerEntidad(entidadId);
        return repositorioAtributo.save(new AtributoEntidad(entidad, nombre, tipoDato, clavePrimaria, obligatorio, valorUnico));
    }

    @Transactional
    public AtributoEntidad crearAtributoVisible(Long entidadId, String ownerUserId, String nombre, TipoDato tipoDato, boolean clavePrimaria, boolean obligatorio, boolean valorUnico) {
        EntidadModelo entidad = obtenerEntidad(entidadId);
        exigirPuedeEditarModelo(entidad.getModelo(), ownerUserId);
        return repositorioAtributo.save(new AtributoEntidad(entidad, nombre, tipoDato, clavePrimaria, obligatorio, valorUnico));
    }

    @Transactional
    public AtributoEntidad actualizarAtributo(Long atributoId, String nombre, TipoDato tipoDato, boolean clavePrimaria, boolean obligatorio, boolean valorUnico) {
        AtributoEntidad atributo = obtenerAtributo(atributoId);
        atributo.actualizar(nombre, tipoDato, clavePrimaria, obligatorio, valorUnico);
        return atributo;
    }

    @Transactional
    public AtributoEntidad actualizarAtributoVisible(Long atributoId, String ownerUserId, String nombre, TipoDato tipoDato, boolean clavePrimaria, boolean obligatorio, boolean valorUnico) {
        AtributoEntidad atributo = obtenerAtributo(atributoId);
        exigirPuedeEditarModelo(atributo.getEntidad().getModelo(), ownerUserId);
        atributo.actualizar(nombre, tipoDato, clavePrimaria, obligatorio, valorUnico);
        return atributo;
    }

    @Transactional
    public void eliminarAtributo(Long atributoId) {
        AtributoEntidad atributo = obtenerAtributo(atributoId);
        repositorioAtributo.delete(atributo);
    }

    @Transactional
    public void eliminarAtributoVisible(Long atributoId, String ownerUserId) {
        AtributoEntidad atributo = obtenerAtributo(atributoId);
        exigirPuedeEditarModelo(atributo.getEntidad().getModelo(), ownerUserId);
        repositorioAtributo.delete(atributo);
    }

    @Transactional
    public OperacionEntidad crearOperacionVisible(Long entidadId, String ownerUserId, String nombre, String tipoRetorno, String firma, OperacionEntidad.Visibilidad visibilidad) {
        EntidadModelo entidad = obtenerEntidad(entidadId);
        exigirPuedeEditarModelo(entidad.getModelo(), ownerUserId);
        return repositorioOperacion.save(new OperacionEntidad(entidad, nombre, tipoRetorno, firma, visibilidad));
    }

    @Transactional(readOnly = true)
    public List<OperacionEntidad> listarOperacionesVisibles(Long entidadId, String ownerUserId) {
        EntidadModelo entidad = obtenerEntidad(entidadId);
        exigirProyectoVisible(entidad.getModelo(), ownerUserId);
        return repositorioOperacion.findByEntidadIdOrderByIdAsc(entidadId);
    }

    @Transactional
    public OperacionEntidad actualizarOperacionVisible(Long operacionId, String ownerUserId, String nombre, String tipoRetorno, String firma, OperacionEntidad.Visibilidad visibilidad) {
        OperacionEntidad operacion = obtenerOperacion(operacionId);
        exigirPuedeEditarModelo(operacion.getEntidad().getModelo(), ownerUserId);
        operacion.actualizar(nombre, tipoRetorno, firma, visibilidad);
        return operacion;
    }

    @Transactional
    public void eliminarOperacionVisible(Long operacionId, String ownerUserId) {
        OperacionEntidad operacion = obtenerOperacion(operacionId);
        exigirPuedeEditarModelo(operacion.getEntidad().getModelo(), ownerUserId);
        repositorioOperacion.delete(operacion);
    }

    @Transactional
    public RelacionModelo crearRelacion(Long modeloId, Long entidadOrigenId, Long entidadDestinoId, String nombre, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        return crearRelacion(modeloId, entidadOrigenId, entidadDestinoId, nombre, null, cardinalidadOrigen, cardinalidadDestino);
    }

    @Transactional
    public RelacionModelo crearRelacion(Long modeloId, Long entidadOrigenId, Long entidadDestinoId, String nombre, String verbo, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        return crearRelacion(modeloId, entidadOrigenId, entidadDestinoId, nombre, verbo, TipoRelacion.ASOCIACION, cardinalidadOrigen, cardinalidadDestino);
    }

    @Transactional
    public RelacionModelo crearRelacion(Long modeloId, Long entidadOrigenId, Long entidadDestinoId, String nombre, String verbo, TipoRelacion tipo, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        ModeloConceptual modelo = obtenerModelo(modeloId);
        EntidadModelo origen = obtenerEntidad(entidadOrigenId);
        EntidadModelo destino = obtenerEntidad(entidadDestinoId);
        validarEntidadesDeRelacion(modelo, origen, destino);
        return repositorioRelacion.save(new RelacionModelo(modelo, origen, destino, nombre, verbo, tipo, cardinalidadOrigen, cardinalidadDestino));
    }

    @Transactional
    public RelacionModelo crearRelacionVisible(Long modeloId, String ownerUserId, Long entidadOrigenId, Long entidadDestinoId, String nombre, String verbo, TipoRelacion tipo, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        ModeloConceptual modelo = obtenerModelo(modeloId);
        exigirPuedeEditarModelo(modelo, ownerUserId);
        EntidadModelo origen = obtenerEntidad(entidadOrigenId);
        EntidadModelo destino = obtenerEntidad(entidadDestinoId);
        validarEntidadesDeRelacion(modelo, origen, destino);
        return repositorioRelacion.save(new RelacionModelo(modelo, origen, destino, nombre, verbo, tipo, cardinalidadOrigen, cardinalidadDestino));
    }

    @Transactional
    public RelacionModelo actualizarRelacion(Long relacionId, Long entidadOrigenId, Long entidadDestinoId, String nombre, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        return actualizarRelacion(relacionId, entidadOrigenId, entidadDestinoId, nombre, null, cardinalidadOrigen, cardinalidadDestino);
    }

    @Transactional
    public RelacionModelo actualizarRelacion(Long relacionId, Long entidadOrigenId, Long entidadDestinoId, String nombre, String verbo, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        return actualizarRelacion(relacionId, entidadOrigenId, entidadDestinoId, nombre, verbo, TipoRelacion.ASOCIACION, cardinalidadOrigen, cardinalidadDestino);
    }

    @Transactional
    public RelacionModelo actualizarRelacion(Long relacionId, Long entidadOrigenId, Long entidadDestinoId, String nombre, String verbo, TipoRelacion tipo, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        RelacionModelo relacion = obtenerRelacion(relacionId);
        EntidadModelo origen = obtenerEntidad(entidadOrigenId);
        EntidadModelo destino = obtenerEntidad(entidadDestinoId);
        validarEntidadesDeRelacion(relacion.getModelo(), origen, destino);
        relacion.actualizar(origen, destino, nombre, verbo, tipo, cardinalidadOrigen, cardinalidadDestino);
        return relacion;
    }

    @Transactional
    public RelacionModelo actualizarRelacionVisible(Long relacionId, String ownerUserId, Long entidadOrigenId, Long entidadDestinoId, String nombre, String verbo, TipoRelacion tipo, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        RelacionModelo relacion = obtenerRelacion(relacionId);
        exigirPuedeEditarModelo(relacion.getModelo(), ownerUserId);
        EntidadModelo origen = obtenerEntidad(entidadOrigenId);
        EntidadModelo destino = obtenerEntidad(entidadDestinoId);
        validarEntidadesDeRelacion(relacion.getModelo(), origen, destino);
        relacion.actualizar(origen, destino, nombre, verbo, tipo, cardinalidadOrigen, cardinalidadDestino);
        return relacion;
    }

    @Transactional
    public void eliminarRelacion(Long relacionId) {
        RelacionModelo relacion = obtenerRelacion(relacionId);
        repositorioRelacion.delete(relacion);
    }

    @Transactional
    public void eliminarRelacionVisible(Long relacionId, String ownerUserId) {
        RelacionModelo relacion = obtenerRelacion(relacionId);
        exigirPuedeEditarModelo(relacion.getModelo(), ownerUserId);
        repositorioRelacion.delete(relacion);
    }

    private AtributoEntidad obtenerAtributo(Long atributoId) {
        return repositorioAtributo.findById(atributoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Atributo no encontrado: " + atributoId));
    }

    private OperacionEntidad obtenerOperacion(Long operacionId) {
        return repositorioOperacion.findById(operacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Operacion no encontrada: " + operacionId));
    }

    private RelacionModelo obtenerRelacion(Long relacionId) {
        return repositorioRelacion.findById(relacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Relacion no encontrada: " + relacionId));
    }

    private void exigirProyectoVisible(ModeloConceptual modelo, String ownerUserId) {
        obtenerProyectoVisible(modelo.getProyecto().getId(), ownerUserId);
    }

    private Proyecto obtenerProyectoVisible(Long proyectoId, String actorId) {
        validarActor(actorId);
        Proyecto proyecto = servicioProyectos.obtener(proyectoId);
        if (proyecto.getOwnerUserId().equalsIgnoreCase(actorId.trim())) {
            return proyecto;
        }
        repositorioMiembros.findByProyectoIdAndUserIdIgnoreCase(proyectoId, actorId.trim())
                .filter(MiembroProyecto::activo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenes acceso a este proyecto"));
        return proyecto;
    }

    private void exigirPuedeEditarModelo(ModeloConceptual modelo, String actorId) {
        exigirPuedeEditarProyecto(modelo.getProyecto(), actorId);
    }

    private void exigirPuedeEditarProyecto(Proyecto proyecto, String actorId) {
        validarActor(actorId);
        if (proyecto.getOwnerUserId().equalsIgnoreCase(actorId.trim())) {
            return;
        }
        MiembroProyecto miembro = repositorioMiembros.findByProyectoIdAndUserIdIgnoreCase(proyecto.getId(), actorId.trim())
                .filter(MiembroProyecto::activo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenes permiso para editar este modelo"));
        if (!(miembro.administrador() || miembro.isEditModel())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenes permiso para editar este modelo");
        }
    }

    private void validarActor(String actorId) {
        if (actorId == null || actorId.isBlank()) {
            throw new ReglaNegocioException("El actor autenticado es obligatorio");
        }
    }

    private void validarEntidadesDeRelacion(ModeloConceptual modelo, EntidadModelo origen, EntidadModelo destino) {
        if (!origen.getModelo().getId().equals(modelo.getId()) || !destino.getModelo().getId().equals(modelo.getId())) {
            throw new ReglaNegocioException("Las entidades de la relacion deben pertenecer al mismo modelo");
        }
    }
}
