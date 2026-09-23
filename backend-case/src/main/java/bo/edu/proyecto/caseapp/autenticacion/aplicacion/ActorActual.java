package bo.edu.proyecto.caseapp.autenticacion.aplicacion;

import bo.edu.proyecto.caseapp.autenticacion.dominio.UsuarioAcademico;
import bo.edu.proyecto.caseapp.autenticacion.infraestructura.RepositorioSesionAcademica;
import bo.edu.proyecto.caseapp.autenticacion.infraestructura.RepositorioUsuarioAcademico;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ActorActual {

    private final ServicioAutenticacionAcademica autenticacion;
    private final RepositorioSesionAcademica repositorioSesiones;
    private final RepositorioUsuarioAcademico repositorioUsuarios;

    public ActorActual(
            ServicioAutenticacionAcademica autenticacion,
            RepositorioSesionAcademica repositorioSesiones,
            RepositorioUsuarioAcademico repositorioUsuarios
    ) {
        this.autenticacion = autenticacion;
        this.repositorioSesiones = repositorioSesiones;
        this.repositorioUsuarios = repositorioUsuarios;
    }

    public Optional<UsuarioAcademico> opcional(HttpServletRequest request) {
        Optional<UsuarioAcademico> porSesion = autenticacion.extraerToken(request)
                .flatMap(repositorioSesiones::findByToken)
                .filter(sesion -> sesion.estaVigente(LocalDateTime.now()))
                .map(sesion -> sesion.getUsuario());
        if (porSesion.isPresent()) {
            return porSesion;
        }

        return usuarioLegacyCompatibilidad(request);
    }

    public UsuarioAcademico requerido(HttpServletRequest request) {
        return opcional(request).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Sesion academica requerida o expirada"
        ));
    }

    /**
     * Compatibility fallback for older demo clients. This header is accepted only
     * when it matches an existing active academic user; arbitrary user ids are
     * never materialized or trusted.
     */
    private Optional<UsuarioAcademico> usuarioLegacyCompatibilidad(HttpServletRequest request) {
        String usuarioId = request.getHeader("X-User-Id");
        if (usuarioId == null || usuarioId.isBlank()) {
            return Optional.empty();
        }
        return repositorioUsuarios.findByUsuarioIdIgnoreCase(usuarioId.trim())
                .filter(UsuarioAcademico::isActivo);
    }
}
