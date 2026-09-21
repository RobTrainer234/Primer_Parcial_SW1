package bo.edu.proyecto.caseapp.proyectos.presentacion;

import bo.edu.proyecto.caseapp.proyectos.dominio.EstadoProyecto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SolicitudProyecto(
        @NotBlank @Size(max = 120) String nombre,
        @Size(max = 1000) String descripcion,
        EstadoProyecto estado
) {
}
