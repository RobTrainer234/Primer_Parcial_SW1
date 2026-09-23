package bo.edu.proyecto.caseapp.generacion.presentacion;

import bo.edu.proyecto.caseapp.generacion.dominio.EstadoGeneracion;
import bo.edu.proyecto.caseapp.generacion.dominio.TrabajoGeneracion;
import java.time.LocalDateTime;

public record RespuestaGeneracion(
        Long id,
        Long modeloId,
        EstadoGeneracion estado,
        LocalDateTime iniciadoEn,
        LocalDateTime finalizadoEn,
        String mensajeError,
        String target,
        String perfil,
        String artifactHash
) {
    public static RespuestaGeneracion desde(TrabajoGeneracion trabajo) {
        return desde(trabajo, null);
    }

    public static RespuestaGeneracion desde(TrabajoGeneracion trabajo, String artifactHash) {
        return new RespuestaGeneracion(
                trabajo.getId(),
                trabajo.getModelo().getId(),
                trabajo.getEstado(),
                trabajo.getIniciadoEn(),
                trabajo.getFinalizadoEn(),
                trabajo.getMensajeError(),
                "spring-boot",
                "default",
                artifactHash
        );
    }
}
