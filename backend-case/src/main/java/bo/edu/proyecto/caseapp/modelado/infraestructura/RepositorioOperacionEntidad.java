package bo.edu.proyecto.caseapp.modelado.infraestructura;

import bo.edu.proyecto.caseapp.modelado.dominio.OperacionEntidad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioOperacionEntidad extends JpaRepository<OperacionEntidad, Long> {
    List<OperacionEntidad> findByEntidadIdOrderByIdAsc(Long entidadId);
}
