package bo.edu.proyecto.caseapp.proyectos.infraestructura;

import bo.edu.proyecto.caseapp.proyectos.dominio.EventoPermisoDiagrama;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioEventoPermisoDiagrama extends JpaRepository<EventoPermisoDiagrama, Long> {
    List<EventoPermisoDiagrama> findByProyectoIdAndDiagramaIdOrderByOccurredAtDesc(Long proyectoId, Long diagramaId);
}
