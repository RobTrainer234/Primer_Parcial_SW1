package bo.edu.proyecto.caseapp.modelado.infraestructura;

import bo.edu.proyecto.caseapp.modelado.dominio.AtributoEntidad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioAtributoEntidad extends JpaRepository<AtributoEntidad, Long> {
    List<AtributoEntidad> findByEntidadId(Long entidadId);
}
