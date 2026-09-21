package bo.edu.proyecto.caseapp.modelado.presentacion;

import bo.edu.proyecto.caseapp.modelado.dominio.Cardinalidad;
import bo.edu.proyecto.caseapp.modelado.dominio.TipoDato;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class SolicitudesModelado {
    private SolicitudesModelado() {}

    public record SolicitudModelo(@NotBlank @Size(max = 120) String nombre) {}

    public record SolicitudEntidad(
            @NotBlank @Size(max = 120) String nombre,
            Integer posicionX,
            Integer posicionY
    ) {}

    public record SolicitudAtributo(
            @NotBlank @Size(max = 120) String nombre,
            @NotNull TipoDato tipoDato,
            boolean clavePrimaria,
            boolean obligatorio,
            boolean valorUnico
    ) {}

    public record SolicitudRelacion(
            @NotNull Long entidadOrigenId,
            @NotNull Long entidadDestinoId,
            @NotBlank @Size(max = 120) String nombre,
            @NotNull Cardinalidad cardinalidadOrigen,
            @NotNull Cardinalidad cardinalidadDestino
    ) {}
}
