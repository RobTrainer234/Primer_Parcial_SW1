package bo.edu.proyecto.caseapp.almacenamiento.presentacion;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ControladorAlmacenamiento {
    private final String provider;
    private final String s3Bucket;
    private final String flociEndpoint;

    public ControladorAlmacenamiento(
            @Value("${caseapp.storage.provider:local}") String provider,
            @Value("${caseapp.storage.s3.bucket:}") String s3Bucket,
            @Value("${caseapp.storage.floci.endpoint:}") String flociEndpoint) {
        this.provider = provider;
        this.s3Bucket = s3Bucket;
        this.flociEndpoint = flociEndpoint;
    }

    @GetMapping({"/storage/status", "/projects/{projectId}/storage/status"})
    public EstadoAlmacenamiento estado() {
        String normalizado = provider == null || provider.isBlank() ? "local" : provider.trim().toLowerCase();
        ProveedorAlmacenamiento local = new ProveedorAlmacenamiento("local", normalizado.equals("local"), true, "Artefactos ZIP locales preservados; descarga por /generaciones/{id}/artefacto.");
        ProveedorAlmacenamiento s3 = new ProveedorAlmacenamiento("s3", normalizado.equals("s3"), !s3Bucket.isBlank(), s3Bucket.isBlank() ? "Opt-in placeholder: falta caseapp.storage.s3.bucket y credenciales/servicio." : "Configuracion declarada; sin dependencia AWS pesada ni subida real en esta fase.");
        ProveedorAlmacenamiento floci = new ProveedorAlmacenamiento("floci", normalizado.equals("floci"), !flociEndpoint.isBlank(), flociEndpoint.isBlank() ? "Opt-in placeholder: falta caseapp.storage.floci.endpoint y servicio disponible." : "Endpoint declarado; integracion real queda fuera de la demo local.");
        return new EstadoAlmacenamiento(
                normalizado,
                normalizado.equals("local") ? "AVAILABLE" : "CONFIG_DECLARED_ONLY",
                "CU18 conserva generacion/descarga local. S3/Floci se exponen como configuracion opt-in honesta, sin prometer disponibilidad productiva.",
                List.of(local, s3, floci)
        );
    }

    public record EstadoAlmacenamiento(String activeProvider, String status, String limitation, List<ProveedorAlmacenamiento> providers) {}
    public record ProveedorAlmacenamiento(String id, boolean selected, boolean configured, String detail) {}
}
