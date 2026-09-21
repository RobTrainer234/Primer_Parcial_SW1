package bo.edu.proyecto.caseapp.proyectos.aplicacion;

import bo.edu.proyecto.caseapp.compartido.dominio.RecursoNoEncontradoException;
import bo.edu.proyecto.caseapp.compartido.dominio.ReglaNegocioException;
import bo.edu.proyecto.caseapp.proyectos.dominio.EstadoProyecto;
import bo.edu.proyecto.caseapp.proyectos.dominio.Proyecto;
import bo.edu.proyecto.caseapp.proyectos.infraestructura.RepositorioProyecto;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioProyectos {

    private final RepositorioProyecto repositorioProyecto;

    public ServicioProyectos(RepositorioProyecto repositorioProyecto) {
        this.repositorioProyecto = repositorioProyecto;
    }

    @Transactional
    public Proyecto crear(String nombre, String descripcion) {
        if (repositorioProyecto.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe un proyecto con ese nombre");
        }
        return repositorioProyecto.save(new Proyecto(nombre, descripcion));
    }

    @Transactional(readOnly = true)
    public List<Proyecto> listar() {
        return repositorioProyecto.findAll();
    }

    @Transactional(readOnly = true)
    public Proyecto obtener(Long id) {
        return repositorioProyecto.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado: " + id));
    }

    @Transactional
    public Proyecto actualizar(Long id, String nombre, String descripcion, EstadoProyecto estado) {
        Proyecto proyecto = obtener(id);
        proyecto.actualizar(nombre, descripcion, estado);
        return proyecto;
    }

    @Transactional
    public void eliminar(Long id) {
        Proyecto proyecto = obtener(id);
        repositorioProyecto.delete(proyecto);
    }
}
