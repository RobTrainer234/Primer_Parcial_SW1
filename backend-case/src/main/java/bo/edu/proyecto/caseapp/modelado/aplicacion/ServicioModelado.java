package bo.edu.proyecto.caseapp.modelado.aplicacion;

import bo.edu.proyecto.caseapp.compartido.dominio.RecursoNoEncontradoException;
import bo.edu.proyecto.caseapp.compartido.dominio.ReglaNegocioException;
import bo.edu.proyecto.caseapp.modelado.dominio.AtributoEntidad;
import bo.edu.proyecto.caseapp.modelado.dominio.Cardinalidad;
import bo.edu.proyecto.caseapp.modelado.dominio.EntidadModelo;
import bo.edu.proyecto.caseapp.modelado.dominio.ModeloConceptual;
import bo.edu.proyecto.caseapp.modelado.dominio.RelacionModelo;
import bo.edu.proyecto.caseapp.modelado.dominio.TipoDato;
import bo.edu.proyecto.caseapp.modelado.infraestructura.RepositorioAtributoEntidad;
import bo.edu.proyecto.caseapp.modelado.infraestructura.RepositorioEntidadModelo;
import bo.edu.proyecto.caseapp.modelado.infraestructura.RepositorioModeloConceptual;
import bo.edu.proyecto.caseapp.modelado.infraestructura.RepositorioRelacionModelo;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioProyectos;
import bo.edu.proyecto.caseapp.proyectos.dominio.Proyecto;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioModelado {

    private final ServicioProyectos servicioProyectos;
    private final RepositorioModeloConceptual repositorioModelo;
    private final RepositorioEntidadModelo repositorioEntidad;
    private final RepositorioAtributoEntidad repositorioAtributo;
    private final RepositorioRelacionModelo repositorioRelacion;

    public ServicioModelado(ServicioProyectos servicioProyectos, RepositorioModeloConceptual repositorioModelo, RepositorioEntidadModelo repositorioEntidad, RepositorioAtributoEntidad repositorioAtributo, RepositorioRelacionModelo repositorioRelacion) {
        this.servicioProyectos = servicioProyectos;
        this.repositorioModelo = repositorioModelo;
        this.repositorioEntidad = repositorioEntidad;
        this.repositorioAtributo = repositorioAtributo;
        this.repositorioRelacion = repositorioRelacion;
    }

    @Transactional
    public ModeloConceptual crearModelo(Long proyectoId, String nombre) {
        Proyecto proyecto = servicioProyectos.obtener(proyectoId);
        return repositorioModelo.save(new ModeloConceptual(proyecto, nombre));
    }

    @Transactional(readOnly = true)
    public List<ModeloConceptual> listarModelos(Long proyectoId) {
        servicioProyectos.obtener(proyectoId);
        return repositorioModelo.findByProyectoId(proyectoId);
    }

    @Transactional(readOnly = true)
    public ModeloConceptual obtenerModelo(Long modeloId) {
        return repositorioModelo.findById(modeloId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Modelo no encontrado: " + modeloId));
    }

    @Transactional(readOnly = true)
    public ModeloConceptual obtenerPrimerModeloPorProyecto(Long proyectoId) {
        servicioProyectos.obtener(proyectoId);
        return repositorioModelo.findFirstByProyectoIdOrderByIdAsc(proyectoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto no tiene modelo conceptual"));
    }

    @Transactional
    public EntidadModelo crearEntidad(Long modeloId, String nombre, Integer posicionX, Integer posicionY) {
        ModeloConceptual modelo = obtenerModelo(modeloId);
        EntidadModelo entidad = new EntidadModelo(modelo, nombre, posicionX, posicionY);
        return repositorioEntidad.save(entidad);
    }

    @Transactional
    public EntidadModelo actualizarEntidad(Long entidadId, String nombre, Integer posicionX, Integer posicionY) {
        EntidadModelo entidad = obtenerEntidad(entidadId);
        entidad.actualizar(nombre, posicionX, posicionY);
        return entidad;
    }

    @Transactional(readOnly = true)
    public EntidadModelo obtenerEntidad(Long entidadId) {
        return repositorioEntidad.findById(entidadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entidad no encontrada: " + entidadId));
    }

    @Transactional
    public void eliminarEntidad(Long entidadId) {
        repositorioEntidad.delete(obtenerEntidad(entidadId));
    }

    @Transactional
    public AtributoEntidad crearAtributo(Long entidadId, String nombre, TipoDato tipoDato, boolean clavePrimaria, boolean obligatorio, boolean valorUnico) {
        EntidadModelo entidad = obtenerEntidad(entidadId);
        return repositorioAtributo.save(new AtributoEntidad(entidad, nombre, tipoDato, clavePrimaria, obligatorio, valorUnico));
    }

    @Transactional
    public AtributoEntidad actualizarAtributo(Long atributoId, String nombre, TipoDato tipoDato, boolean clavePrimaria, boolean obligatorio, boolean valorUnico) {
        AtributoEntidad atributo = repositorioAtributo.findById(atributoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Atributo no encontrado: " + atributoId));
        atributo.actualizar(nombre, tipoDato, clavePrimaria, obligatorio, valorUnico);
        return atributo;
    }

    @Transactional
    public void eliminarAtributo(Long atributoId) {
        AtributoEntidad atributo = repositorioAtributo.findById(atributoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Atributo no encontrado: " + atributoId));
        repositorioAtributo.delete(atributo);
    }

    @Transactional
    public RelacionModelo crearRelacion(Long modeloId, Long entidadOrigenId, Long entidadDestinoId, String nombre, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        ModeloConceptual modelo = obtenerModelo(modeloId);
        EntidadModelo origen = obtenerEntidad(entidadOrigenId);
        EntidadModelo destino = obtenerEntidad(entidadDestinoId);
        validarEntidadesDeRelacion(modelo, origen, destino);
        return repositorioRelacion.save(new RelacionModelo(modelo, origen, destino, nombre, cardinalidadOrigen, cardinalidadDestino));
    }

    @Transactional
    public RelacionModelo actualizarRelacion(Long relacionId, Long entidadOrigenId, Long entidadDestinoId, String nombre, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        RelacionModelo relacion = repositorioRelacion.findById(relacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Relacion no encontrada: " + relacionId));
        EntidadModelo origen = obtenerEntidad(entidadOrigenId);
        EntidadModelo destino = obtenerEntidad(entidadDestinoId);
        validarEntidadesDeRelacion(relacion.getModelo(), origen, destino);
        relacion.actualizar(origen, destino, nombre, cardinalidadOrigen, cardinalidadDestino);
        return relacion;
    }

    @Transactional
    public void eliminarRelacion(Long relacionId) {
        RelacionModelo relacion = repositorioRelacion.findById(relacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Relacion no encontrada: " + relacionId));
        repositorioRelacion.delete(relacion);
    }

    private void validarEntidadesDeRelacion(ModeloConceptual modelo, EntidadModelo origen, EntidadModelo destino) {
        if (!origen.getModelo().getId().equals(modelo.getId()) || !destino.getModelo().getId().equals(modelo.getId())) {
            throw new ReglaNegocioException("Las entidades de la relacion deben pertenecer al mismo modelo");
        }
    }
}
