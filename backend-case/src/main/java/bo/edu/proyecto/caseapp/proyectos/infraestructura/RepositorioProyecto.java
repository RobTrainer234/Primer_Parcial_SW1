package bo.edu.proyecto.caseapp.proyectos.infraestructura;

import bo.edu.proyecto.caseapp.proyectos.dominio.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioProyecto extends JpaRepository<Proyecto, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
