package bo.edu.proyecto.caseapp.proyectos.infraestructura;

import bo.edu.proyecto.caseapp.proyectos.dominio.ColaboradorDiagrama;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioColaboradorDiagrama extends JpaRepository<ColaboradorDiagrama, Long> {
    Optional<ColaboradorDiagrama> findByDiagramaIdAndUserIdIgnoreCase(Long diagramaId, String userId);
    List<ColaboradorDiagrama> findByDiagramaIdOrderByUserIdAsc(Long diagramaId);
}
