package bo.edu.proyecto.caseapp.modelado.infraestructura;

import bo.edu.proyecto.caseapp.modelado.dominio.ModeloConceptual;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioModeloConceptual extends JpaRepository<ModeloConceptual, Long> {
    List<ModeloConceptual> findByProyectoId(Long proyectoId);
    Optional<ModeloConceptual> findFirstByProyectoIdOrderByIdAsc(Long proyectoId);
}
