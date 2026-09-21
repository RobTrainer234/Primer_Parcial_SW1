package bo.edu.proyecto.caseapp.validacion.dominio;

import java.util.List;

public record ResultadoValidacion(boolean valido, List<ErrorValidacion> errores) {
    public static ResultadoValidacion desde(List<ErrorValidacion> errores) {
        return new ResultadoValidacion(errores.isEmpty(), errores);
    }
}
