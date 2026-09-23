package bo.edu.proyecto.caseapp.validacion.dominio;

import java.util.List;

public record ResultadoValidacion(boolean valido, List<ErrorValidacion> errores, String perfil, List<String> reglasAplicadas) {
    public static final String PERFIL_UML_25_CLASES_SUBCONJUNTO = "UML_2_5_CLASES_SUBCONJUNTO";

    public static ResultadoValidacion desde(List<ErrorValidacion> errores) {
        return new ResultadoValidacion(errores.isEmpty(), errores, PERFIL_UML_25_CLASES_SUBCONJUNTO, List.of(
                "UML25-CLASS-001",
                "UML25-CLASS-002",
                "UML25-PROP-001",
                "UML25-PROP-002",
                "UML25-PROP-003",
                "UML25-ASSOC-001",
                "UML25-ASSOC-002",
                "UML25-ASSOC-003",
                "UML25-ASSOC-004",
                "UML25-MODEL-001"
        ));
    }
}
