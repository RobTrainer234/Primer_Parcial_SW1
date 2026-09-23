package bo.edu.proyecto.caseapp.autenticacion.aplicacion;

import bo.edu.proyecto.caseapp.autenticacion.dominio.SesionAcademica;
import bo.edu.proyecto.caseapp.autenticacion.dominio.UsuarioAcademico;
import bo.edu.proyecto.caseapp.autenticacion.infraestructura.RepositorioSesionAcademica;
import bo.edu.proyecto.caseapp.autenticacion.infraestructura.RepositorioUsuarioAcademico;
import bo.edu.proyecto.caseapp.autenticacion.presentacion.ControladorAutenticacion.RespuestaSesion;
import jakarta.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServicioAutenticacionAcademica {

    private static final String USUARIO_DEMO_POR_DEFECTO = "demo-admin";
    private static final int HORAS_VIGENCIA_SESION = 8;
    private static final String ADVERTENCIA_ACADEMICA = "Sesion academica persistente para demo: tokenDemo es opaco y no representa autenticacion productiva.";

    private final RepositorioUsuarioAcademico repositorioUsuarios;
    private final RepositorioSesionAcademica repositorioSesiones;
    private final SecureRandom secureRandom = new SecureRandom();

    public ServicioAutenticacionAcademica(
            RepositorioUsuarioAcademico repositorioUsuarios,
            RepositorioSesionAcademica repositorioSesiones
    ) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.repositorioSesiones = repositorioSesiones;
    }

    @Transactional
    public RespuestaSesion login(String usuarioSolicitado) {
        String usuarioId = normalizarUsuario(usuarioSolicitado);
        UsuarioAcademico usuario = repositorioUsuarios.findByUsuarioIdIgnoreCase(usuarioId)
                .filter(UsuarioAcademico::isActivo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario academico inexistente o inactivo"
                ));

        SesionAcademica sesion = new SesionAcademica(
                generarTokenOpaco(),
                usuario,
                LocalDateTime.now().plusHours(HORAS_VIGENCIA_SESION)
        );
        repositorioSesiones.save(sesion);
        return respuesta(usuario, sesion.getToken());
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        extraerToken(request)
                .flatMap(repositorioSesiones::findByToken)
                .ifPresent(sesion -> sesion.revocar(LocalDateTime.now()));
    }

    public Optional<String> extraerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            String token = authorization.substring(7).trim();
            if (!token.isBlank()) {
                return Optional.of(token);
            }
        }
        String tokenSesion = request.getHeader("X-Session-Token");
        if (tokenSesion != null && !tokenSesion.isBlank()) {
            return Optional.of(tokenSesion.trim());
        }
        return Optional.empty();
    }

    private String normalizarUsuario(String usuarioSolicitado) {
        if (usuarioSolicitado == null || usuarioSolicitado.isBlank()) {
            return USUARIO_DEMO_POR_DEFECTO;
        }
        return usuarioSolicitado.trim().toLowerCase();
    }

    private String generarTokenOpaco() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private RespuestaSesion respuesta(UsuarioAcademico usuario, String token) {
        return new RespuestaSesion(
                usuario.getUsuarioId(),
                usuario.getNombreVisible(),
                token,
                true,
                ADVERTENCIA_ACADEMICA
        );
    }
}
