package bo.edu.proyecto.caseapp.despliegues.presentacion;

import bo.edu.proyecto.caseapp.despliegues.aplicacion.ServicioDespliegues;
import bo.edu.proyecto.caseapp.despliegues.aplicacion.ServicioDespliegues.DeploymentDemo;
import bo.edu.proyecto.caseapp.despliegues.aplicacion.ServicioDespliegues.SolicitudDeployment;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/deployments")
public class ControladorDespliegues {
    private final ServicioDespliegues servicioDespliegues;

    public ControladorDespliegues(ServicioDespliegues servicioDespliegues) {
        this.servicioDespliegues = servicioDespliegues;
    }

    @PostMapping
    public ResponseEntity<DeploymentDemo> crear(@PathVariable Long projectId, @RequestBody(required = false) SolicitudDeployment solicitud) {
        DeploymentDemo deployment = servicioDespliegues.crear(projectId, solicitud);
        return ResponseEntity.created(URI.create("/projects/" + projectId + "/deployments/" + deployment.id())).body(deployment);
    }

    @GetMapping
    public List<DeploymentDemo> listar(@PathVariable Long projectId) {
        return servicioDespliegues.listar(projectId);
    }

    @GetMapping("/{deploymentId}")
    public DeploymentDemo obtener(@PathVariable Long projectId, @PathVariable Long deploymentId) {
        return servicioDespliegues.obtener(projectId, deploymentId);
    }

    @PutMapping("/{deploymentId}")
    public DeploymentDemo actualizar(@PathVariable Long projectId, @PathVariable Long deploymentId, @RequestBody(required = false) SolicitudDeployment solicitud) {
        return servicioDespliegues.actualizar(projectId, deploymentId, solicitud);
    }

    @PostMapping("/{deploymentId}/disable")
    public DeploymentDemo deshabilitar(@PathVariable Long projectId, @PathVariable Long deploymentId) {
        return servicioDespliegues.habilitar(projectId, deploymentId, false);
    }

    @PostMapping("/{deploymentId}/enable")
    public DeploymentDemo habilitar(@PathVariable Long projectId, @PathVariable Long deploymentId) {
        return servicioDespliegues.habilitar(projectId, deploymentId, true);
    }

    @DeleteMapping("/{deploymentId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long projectId, @PathVariable Long deploymentId) {
        servicioDespliegues.eliminar(projectId, deploymentId);
        return ResponseEntity.noContent().build();
    }
}
