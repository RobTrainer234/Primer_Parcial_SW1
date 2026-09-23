package bo.edu.proyecto.caseapp.despliegues.aplicacion;

import bo.edu.proyecto.caseapp.compartido.dominio.RecursoNoEncontradoException;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioProyectos;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class ServicioDespliegues {
    private final ServicioProyectos servicioProyectos;
    private final AtomicLong secuencia = new AtomicLong(1);
    private final Map<Long, DeploymentDemo> despliegues = new ConcurrentHashMap<>();

    public ServicioDespliegues(ServicioProyectos servicioProyectos) {
        this.servicioProyectos = servicioProyectos;
    }

    public DeploymentDemo crear(Long projectId, SolicitudDeployment solicitud) {
        servicioProyectos.obtener(projectId);
        Instant ahora = Instant.now();
        DeploymentDemo deployment = new DeploymentDemo(
                secuencia.getAndIncrement(),
                projectId,
                valor(solicitud == null ? null : solicitud.name(), "Deployment demo"),
                valor(solicitud == null ? null : solicitud.environment(), "demo"),
                valor(solicitud == null ? null : solicitud.target(), "local-artifact"),
                true,
                "DEMO_READY",
                "Implementacion academica en memoria; no ejecuta infraestructura real.",
                ahora,
                ahora
        );
        despliegues.put(deployment.id(), deployment);
        return deployment;
    }

    public List<DeploymentDemo> listar(Long projectId) {
        servicioProyectos.obtener(projectId);
        return despliegues.values().stream()
                .filter(deployment -> deployment.projectId().equals(projectId))
                .sorted(Comparator.comparing(DeploymentDemo::id))
                .toList();
    }

    public DeploymentDemo obtener(Long projectId, Long deploymentId) {
        servicioProyectos.obtener(projectId);
        DeploymentDemo deployment = despliegues.get(deploymentId);
        if (deployment == null || !deployment.projectId().equals(projectId)) {
            throw new RecursoNoEncontradoException("Deployment no encontrado: " + deploymentId);
        }
        return deployment;
    }

    public DeploymentDemo actualizar(Long projectId, Long deploymentId, SolicitudDeployment solicitud) {
        DeploymentDemo actual = obtener(projectId, deploymentId);
        DeploymentDemo actualizado = new DeploymentDemo(
                actual.id(),
                actual.projectId(),
                valor(solicitud == null ? null : solicitud.name(), actual.name()),
                valor(solicitud == null ? null : solicitud.environment(), actual.environment()),
                valor(solicitud == null ? null : solicitud.target(), actual.target()),
                actual.enabled(),
                actual.status(),
                "Configuracion actualizada en memoria para demo academica.",
                actual.createdAt(),
                Instant.now()
        );
        despliegues.put(deploymentId, actualizado);
        return actualizado;
    }

    public DeploymentDemo habilitar(Long projectId, Long deploymentId, boolean enabled) {
        DeploymentDemo actual = obtener(projectId, deploymentId);
        DeploymentDemo actualizado = new DeploymentDemo(
                actual.id(), actual.projectId(), actual.name(), actual.environment(), actual.target(), enabled,
                enabled ? "DEMO_READY" : "DISABLED",
                enabled ? "Deployment demo habilitado; no ejecuta infraestructura real." : "Deployment demo deshabilitado en memoria.",
                actual.createdAt(), Instant.now()
        );
        despliegues.put(deploymentId, actualizado);
        return actualizado;
    }

    public void eliminar(Long projectId, Long deploymentId) {
        obtener(projectId, deploymentId);
        despliegues.remove(deploymentId);
    }

    private static String valor(String candidato, String fallback) {
        return candidato == null || candidato.isBlank() ? fallback : candidato.trim();
    }

    public record SolicitudDeployment(String name, String environment, String target) {}
    public record DeploymentDemo(Long id, Long projectId, String name, String environment, String target, boolean enabled, String status, String limitation, Instant createdAt, Instant updatedAt) {}
}
