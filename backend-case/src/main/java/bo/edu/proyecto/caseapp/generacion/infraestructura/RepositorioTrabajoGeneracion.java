package bo.edu.proyecto.caseapp.generacion.infraestructura;

import bo.edu.proyecto.caseapp.generacion.dominio.TrabajoGeneracion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioTrabajoGeneracion extends JpaRepository<TrabajoGeneracion, Long> {
}
