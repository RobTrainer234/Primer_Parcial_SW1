package bo.edu.proyecto.caseapp.modelado.presentacion;

import bo.edu.proyecto.caseapp.modelado.aplicacion.ServicioXmi;
import bo.edu.proyecto.caseapp.modelado.aplicacion.ServicioXmi.ConfirmacionXmi;
import bo.edu.proyecto.caseapp.modelado.aplicacion.ServicioXmi.ExportacionXmi;
import bo.edu.proyecto.caseapp.modelado.aplicacion.ServicioXmi.VistaPreviaXmi;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/models/{modelId}/xmi")
public class ControladorXmi {
    private final ServicioXmi servicioXmi;

    public ControladorXmi(ServicioXmi servicioXmi) {
        this.servicioXmi = servicioXmi;
    }

    @PostMapping("/export")
    public ExportacionXmi exportar(@PathVariable Long projectId, @PathVariable Long modelId) {
        return servicioXmi.exportar(projectId, modelId);
    }

    @PostMapping("/import/preview")
    public VistaPreviaXmi previsualizar(@PathVariable Long projectId, @PathVariable Long modelId, @RequestBody SolicitudPreviewXmi solicitud) {
        return servicioXmi.previsualizar(projectId, modelId, solicitud.xmi());
    }

    @PostMapping("/import/confirm")
    public ConfirmacionXmi confirmar(@PathVariable Long projectId, @PathVariable Long modelId, @RequestBody SolicitudConfirmacionXmi solicitud) {
        return servicioXmi.confirmar(projectId, modelId, solicitud.previewToken(), solicitud.confirmPartialImport());
    }

    public record SolicitudPreviewXmi(@NotBlank String xmi) {}
    public record SolicitudConfirmacionXmi(@NotBlank String previewToken, boolean confirmPartialImport) {}
}
