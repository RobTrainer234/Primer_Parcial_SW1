package bo.edu.proyecto.caseapp.proyectos.infraestructura;

import bo.edu.proyecto.caseapp.proyectos.dominio.InvitacionProyecto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioInvitacionProyecto extends JpaRepository<InvitacionProyecto, Long> {
    List<InvitacionProyecto> findByProyectoIdOrderByCreatedAtDesc(Long proyectoId);
    Optional<InvitacionProyecto> findByProyectoIdAndId(Long proyectoId, Long id);
    boolean existsByProyectoIdAndInviteeUserIdIgnoreCaseAndStatus(Long proyectoId, String inviteeUserId, InvitacionProyecto.Estado status);
}
