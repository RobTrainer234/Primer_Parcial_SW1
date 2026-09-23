package bo.edu.proyecto.caseapp.autenticacion.infraestructura;

import bo.edu.proyecto.caseapp.autenticacion.dominio.SesionAcademica;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioSesionAcademica extends JpaRepository<SesionAcademica, Long> {
    Optional<SesionAcademica> findByToken(String token);
}
