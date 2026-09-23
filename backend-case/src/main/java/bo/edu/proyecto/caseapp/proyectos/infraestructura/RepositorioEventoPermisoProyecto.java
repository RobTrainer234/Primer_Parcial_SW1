package bo.edu.proyecto.caseapp.proyectos.infraestructura;

import bo.edu.proyecto.caseapp.proyectos.dominio.EventoPermisoProyecto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioEventoPermisoProyecto extends JpaRepository<EventoPermisoProyecto, Long> {
    List<EventoPermisoProyecto> findByProyectoIdOrderByOccurredAtDesc(Long proyectoId);
    List<EventoPermisoProyecto> findByProyectoIdAndActionStartingWithAndTargetOrderByOccurredAtDesc(Long proyectoId, String actionPrefix, String target);
}
