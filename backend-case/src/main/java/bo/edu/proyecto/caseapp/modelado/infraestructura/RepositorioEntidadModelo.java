package bo.edu.proyecto.caseapp.modelado.infraestructura;

import bo.edu.proyecto.caseapp.modelado.dominio.EntidadModelo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioEntidadModelo extends JpaRepository<EntidadModelo, Long> {
    List<EntidadModelo> findByModeloId(Long modeloId);
}
