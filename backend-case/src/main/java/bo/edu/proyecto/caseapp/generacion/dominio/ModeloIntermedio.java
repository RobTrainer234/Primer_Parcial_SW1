package bo.edu.proyecto.caseapp.generacion.dominio;

import java.util.List;

public record ModeloIntermedio(
        Long modeloId,
        String nombreAplicacion,
        String paqueteBase,
        List<EntidadIntermedia> entidades,
        List<RelacionIntermedia> relaciones
) {
    public record EntidadIntermedia(
            String nombreOriginal,
            String nombreClase,
            String nombreVariable,
            String rutaRecurso,
            List<AtributoIntermedio> atributos
    ) {
        public AtributoIntermedio clavePrimaria() {
            return atributos.stream()
                    .filter(AtributoIntermedio::clavePrimaria)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Entidad sin clave primaria: " + nombreClase));
        }
    }

    public record AtributoIntermedio(
            String nombreOriginal,
            String nombreCampo,
            String nombreMetodo,
            String tipoJava,
            boolean clavePrimaria,
            boolean obligatorio,
            boolean valorUnico
    ) {
    }

    public record RelacionIntermedia(
            String nombre,
            String entidadOrigen,
            String entidadDestino,
            String cardinalidadOrigen,
            String cardinalidadDestino
    ) {
    }
}
