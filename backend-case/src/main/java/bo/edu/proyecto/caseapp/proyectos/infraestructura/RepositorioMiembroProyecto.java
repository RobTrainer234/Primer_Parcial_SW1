package bo.edu.proyecto.caseapp.proyectos.infraestructura;

import bo.edu.proyecto.caseapp.proyectos.dominio.MiembroProyecto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioMiembroProyecto extends JpaRepository<MiembroProyecto, Long> {
    Optional<MiembroProyecto> findByProyectoIdAndUserIdIgnoreCase(Long proyectoId, String userId);
    List<MiembroProyecto> findByProyectoIdOrderByUserIdAsc(Long proyectoId);
    boolean existsByProyectoIdAndUserIdIgnoreCaseAndStatus(Long proyectoId, String userId, MiembroProyecto.Estado status);
}
