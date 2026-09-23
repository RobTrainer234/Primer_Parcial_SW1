package bo.edu.proyecto.caseapp.autenticacion.infraestructura;

import bo.edu.proyecto.caseapp.autenticacion.dominio.UsuarioAcademico;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioUsuarioAcademico extends JpaRepository<UsuarioAcademico, String> {
    Optional<UsuarioAcademico> findByUsuarioIdIgnoreCase(String usuarioId);
}
