package bo.edu.proyecto.caseapp.proyectos.aplicacion;

import bo.edu.proyecto.caseapp.compartido.dominio.RecursoNoEncontradoException;
import bo.edu.proyecto.caseapp.compartido.dominio.ReglaNegocioException;
import bo.edu.proyecto.caseapp.proyectos.dominio.EstadoProyecto;
import bo.edu.proyecto.caseapp.proyectos.dominio.Proyecto;
import bo.edu.proyecto.caseapp.proyectos.infraestructura.RepositorioProyecto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServicioProyectos {

    private final RepositorioProyecto repositorioProyecto;

    public ServicioProyectos(RepositorioProyecto repositorioProyecto) {
        this.repositorioProyecto = repositorioProyecto;
    }

    @Transactional
    public Proyecto crear(String nombre, String descripcion, String ownerUserId) {
        validarOwner(ownerUserId);
        if (repositorioProyecto.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe un proyecto con ese nombre");
        }
        return repositorioProyecto.save(new Proyecto(nombre, descripcion, ownerUserId));
    }

    @Transactional(readOnly = true)
    public List<Proyecto> listar() {
        return repositorioProyecto.findAll();
    }

    @Transactional(readOnly = true)
    public List<Proyecto> listarVisibles(String ownerUserId) {
        validarOwner(ownerUserId);
        return repositorioProyecto.findByOwnerUserId(ownerUserId.trim());
    }

    @Transactional(readOnly = true)
    public Proyecto obtener(Long id) {
        return repositorioProyecto.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Proyecto obtenerVisible(Long id, String ownerUserId) {
        validarOwner(ownerUserId);
        Proyecto proyecto = obtener(id);
        if (!proyecto.getOwnerUserId().equals(ownerUserId.trim())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenes acceso a este proyecto");
        }
        return proyecto;
    }

    @Transactional
    public Proyecto actualizar(Long id, String nombre, String descripcion, EstadoProyecto estado) {
        Proyecto proyecto = obtener(id);
        proyecto.actualizar(nombre, descripcion, estado);
        return proyecto;
    }

    @Transactional
    public Proyecto actualizarVisible(Long id, String ownerUserId, String nombre, String descripcion, EstadoProyecto estado) {
        Proyecto proyecto = obtenerVisible(id, ownerUserId);
        proyecto.actualizar(nombre, descripcion, estado);
        return proyecto;
    }

    @Transactional
    public void eliminar(Long id) {
        Proyecto proyecto = obtener(id);
        repositorioProyecto.delete(proyecto);
    }

    @Transactional
    public void eliminarVisible(Long id, String ownerUserId) {
        Proyecto proyecto = obtenerVisible(id, ownerUserId);
        repositorioProyecto.delete(proyecto);
    }

    private void validarOwner(String ownerUserId) {
        if (ownerUserId == null || ownerUserId.isBlank()) {
            throw new ReglaNegocioException("El propietario del proyecto es obligatorio");
        }
    }
}
