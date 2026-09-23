package bo.edu.proyecto.caseapp.modelado.presentacion;

import bo.edu.proyecto.caseapp.modelado.dominio.AtributoEntidad;
import bo.edu.proyecto.caseapp.modelado.dominio.Cardinalidad;
import bo.edu.proyecto.caseapp.modelado.dominio.EntidadModelo;
import bo.edu.proyecto.caseapp.modelado.dominio.ModeloConceptual;
import bo.edu.proyecto.caseapp.modelado.dominio.OperacionEntidad;
import bo.edu.proyecto.caseapp.modelado.dominio.RelacionModelo;
import bo.edu.proyecto.caseapp.modelado.dominio.TipoDato;
import bo.edu.proyecto.caseapp.modelado.dominio.TipoRelacion;
import java.time.Instant;
import java.util.List;

public final class RespuestasModelado {
    private RespuestasModelado() {}

    public record RespuestaModelo(Long id, Long proyectoId, String nombre, Integer version) {
        public static RespuestaModelo desde(ModeloConceptual modelo) {
            return new RespuestaModelo(modelo.getId(), modelo.getProyecto().getId(), modelo.getNombre(), modelo.getVersion());
        }
    }

    public record RespuestaAtributo(Long id, String nombre, TipoDato tipoDato, boolean clavePrimaria, boolean obligatorio, boolean valorUnico) {
        public static RespuestaAtributo desde(AtributoEntidad atributo) {
            return new RespuestaAtributo(atributo.getId(), atributo.getNombre(), atributo.getTipoDato(), atributo.isClavePrimaria(), atributo.isObligatorio(), atributo.isValorUnico());
        }
    }

    public record RespuestaOperacion(Long id, String nombre, String tipoRetorno, String firma, OperacionEntidad.Visibilidad visibilidad, String texto, Instant creadoEn, Instant actualizadoEn) {
        public static RespuestaOperacion desde(OperacionEntidad operacion) {
            return new RespuestaOperacion(operacion.getId(), operacion.getNombre(), operacion.getTipoRetorno(), operacion.getFirma(), operacion.getVisibilidad(), operacion.representacionUml(), operacion.getCreadoEn(), operacion.getActualizadoEn());
        }
    }

    public record RespuestaEntidad(Long id, String nombre, Integer posicionX, Integer posicionY, List<RespuestaAtributo> atributos, List<String> operaciones, List<RespuestaOperacion> detalleOperaciones) {
        public static RespuestaEntidad desde(EntidadModelo entidad) {
            List<RespuestaOperacion> operaciones = entidad.getOperaciones().stream().map(RespuestaOperacion::desde).toList();
            return new RespuestaEntidad(
                    entidad.getId(),
                    entidad.getNombre(),
                    entidad.getPosicionX(),
                    entidad.getPosicionY(),
                    entidad.getAtributos().stream().map(RespuestaAtributo::desde).toList(),
                    operaciones.stream().map(RespuestaOperacion::texto).toList(),
                    operaciones
            );
        }
    }

    public record RespuestaRelacion(Long id, Long entidadOrigenId, Long entidadDestinoId, String nombre, String verbo, TipoRelacion tipo, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        public static RespuestaRelacion desde(RelacionModelo relacion) {
            return new RespuestaRelacion(
                    relacion.getId(),
                    relacion.getEntidadOrigen().getId(),
                    relacion.getEntidadDestino().getId(),
                    relacion.getNombre(),
                    relacion.getVerbo(),
                    relacion.getTipo(),
                    relacion.getCardinalidadOrigen(),
                    relacion.getCardinalidadDestino()
            );
        }
    }

    public record RespuestaModeloCompleto(Long id, Long proyectoId, String nombre, Integer version, List<RespuestaEntidad> entidades, List<RespuestaRelacion> relaciones, String semantica) {
        public static RespuestaModeloCompleto desde(ModeloConceptual modelo) {
            return new RespuestaModeloCompleto(
                    modelo.getId(),
                    modelo.getProyecto().getId(),
                    modelo.getNombre(),
                    modelo.getVersion(),
                    modelo.getEntidades().stream().map(RespuestaEntidad::desde).toList(),
                    modelo.getRelaciones().stream().map(RespuestaRelacion::desde).toList(),
                    "UML-CLASES-MINIMO"
            );
        }
    }
}
