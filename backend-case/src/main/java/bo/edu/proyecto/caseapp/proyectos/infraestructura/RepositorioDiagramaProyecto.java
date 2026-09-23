package bo.edu.proyecto.caseapp.proyectos.infraestructura;

import bo.edu.proyecto.caseapp.proyectos.dominio.DiagramaProyecto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioDiagramaProyecto extends JpaRepository<DiagramaProyecto, Long> {
    Optional<DiagramaProyecto> findByProyectoIdAndIdAndEstado(Long proyectoId, Long id, DiagramaProyecto.Estado estado);
}
