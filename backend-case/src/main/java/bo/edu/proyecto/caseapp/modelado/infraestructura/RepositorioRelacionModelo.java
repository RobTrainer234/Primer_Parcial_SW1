package bo.edu.proyecto.caseapp.modelado.infraestructura;

import bo.edu.proyecto.caseapp.modelado.dominio.RelacionModelo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioRelacionModelo extends JpaRepository<RelacionModelo, Long> {
    List<RelacionModelo> findByModeloId(Long modeloId);
}
