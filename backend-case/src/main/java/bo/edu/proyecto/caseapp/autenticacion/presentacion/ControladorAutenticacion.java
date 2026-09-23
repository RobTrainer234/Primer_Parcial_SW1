package bo.edu.proyecto.caseapp.autenticacion.presentacion;

import bo.edu.proyecto.caseapp.autenticacion.aplicacion.ServicioAutenticacionAcademica;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class ControladorAutenticacion {

    private final ServicioAutenticacionAcademica autenticacion;

    public ControladorAutenticacion(ServicioAutenticacionAcademica autenticacion) {
        this.autenticacion = autenticacion;
    }

    @PostMapping("/login")
    public RespuestaSesion login(@RequestBody(required = false) SolicitudLogin solicitud) {
        String usuario = solicitud != null ? solicitud.usuario() : null;
        return autenticacion.login(usuario);
    }

    @PostMapping("/demo-login")
    public RespuestaSesion demoLogin() {
        return autenticacion.login(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        autenticacion.logout(request);
        return ResponseEntity.ok(Map.of("sesionActiva", false, "cerradoEn", Instant.now().toString()));
    }

    public record SolicitudLogin(String usuario, String clave) {}

    public record RespuestaSesion(
            String usuarioId,
            String nombreVisible,
            String tokenDemo,
            boolean demo,
            String advertencia
    ) {}
}
