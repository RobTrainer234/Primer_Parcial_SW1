package bo.edu.proyecto.caseapp.proyectos.presentacion;

import bo.edu.proyecto.caseapp.proyectos.dominio.EstadoProyecto;
import bo.edu.proyecto.caseapp.proyectos.dominio.Proyecto;
import java.time.LocalDateTime;

public record RespuestaProyecto(
        Long id,
        String nombre,
        String descripcion,
        EstadoProyecto estado,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {
    public static RespuestaProyecto desde(Proyecto proyecto) {
        return new RespuestaProyecto(
                proyecto.getId(),
                proyecto.getNombre(),
                proyecto.getDescripcion(),
                proyecto.getEstado(),
                proyecto.getCreadoEn(),
                proyecto.getActualizadoEn()
        );
    }
}
