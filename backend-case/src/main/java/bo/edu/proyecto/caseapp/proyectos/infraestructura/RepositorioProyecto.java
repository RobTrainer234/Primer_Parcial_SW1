package bo.edu.proyecto.caseapp.proyectos.infraestructura;

import bo.edu.proyecto.caseapp.proyectos.dominio.Proyecto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioProyecto extends JpaRepository<Proyecto, Long> {
    boolean existsByNombreIgnoreCase(String nombre);

    List<Proyecto> findByOwnerUserId(String ownerUserId);
}
