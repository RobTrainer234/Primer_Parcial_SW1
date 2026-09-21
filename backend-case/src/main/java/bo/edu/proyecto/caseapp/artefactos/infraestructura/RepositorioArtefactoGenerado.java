package bo.edu.proyecto.caseapp.artefactos.infraestructura;

import bo.edu.proyecto.caseapp.artefactos.dominio.ArtefactoGenerado;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioArtefactoGenerado extends JpaRepository<ArtefactoGenerado, Long> {
    Optional<ArtefactoGenerado> findByTrabajoGeneracionId(Long trabajoGeneracionId);
}
