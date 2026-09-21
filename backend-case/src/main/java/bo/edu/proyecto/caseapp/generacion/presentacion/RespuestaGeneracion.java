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
        String mensajeError
) {
    public static RespuestaGeneracion desde(TrabajoGeneracion trabajo) {
        return new RespuestaGeneracion(
                trabajo.getId(),
                trabajo.getModelo().getId(),
                trabajo.getEstado(),
                trabajo.getIniciadoEn(),
                trabajo.getFinalizadoEn(),
                trabajo.getMensajeError()
        );
    }
}
