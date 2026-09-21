package bo.edu.proyecto.caseapp.generacion.aplicacion;

import bo.edu.proyecto.caseapp.generacion.dominio.ModeloIntermedio;
import bo.edu.proyecto.caseapp.generacion.dominio.ModeloIntermedio.AtributoIntermedio;
import bo.edu.proyecto.caseapp.generacion.dominio.ModeloIntermedio.EntidadIntermedia;
import bo.edu.proyecto.caseapp.generacion.dominio.ModeloIntermedio.RelacionIntermedia;
import bo.edu.proyecto.caseapp.modelado.dominio.AtributoEntidad;
import bo.edu.proyecto.caseapp.modelado.dominio.EntidadModelo;
import bo.edu.proyecto.caseapp.modelado.dominio.ModeloConceptual;
import bo.edu.proyecto.caseapp.modelado.dominio.RelacionModelo;
import bo.edu.proyecto.caseapp.modelado.dominio.TipoDato;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ConstructorModeloIntermedio {

    private final NormalizadorNombres normalizador;

    public ConstructorModeloIntermedio(NormalizadorNombres normalizador) {
        this.normalizador = normalizador;
    }

    public ModeloIntermedio construir(ModeloConceptual modelo) {
        String nombreAplicacion = normalizador.clase(modelo.getNombre());
        String paqueteBase = normalizador.paquete(modelo.getNombre());
        List<EntidadIntermedia> entidades = modelo.getEntidades().stream().map(this::convertirEntidad).toList();
        List<RelacionIntermedia> relaciones = modelo.getRelaciones().stream().map(this::convertirRelacion).toList();
        return new ModeloIntermedio(modelo.getId(), nombreAplicacion, paqueteBase, entidades, relaciones);
    }

    private EntidadIntermedia convertirEntidad(EntidadModelo entidad) {
        return new EntidadIntermedia(
                entidad.getNombre(),
                normalizador.clase(entidad.getNombre()),
                normalizador.variable(entidad.getNombre()),
                normalizador.recurso(entidad.getNombre()),
                entidad.getAtributos().stream().map(this::convertirAtributo).toList()
        );
    }

    private AtributoIntermedio convertirAtributo(AtributoEntidad atributo) {
        return new AtributoIntermedio(
                atributo.getNombre(),
                normalizador.variable(atributo.getNombre()),
                normalizador.metodo(atributo.getNombre()),
                tipoJava(atributo.getTipoDato()),
                atributo.isClavePrimaria(),
                atributo.isObligatorio(),
                atributo.isValorUnico()
        );
    }

    private RelacionIntermedia convertirRelacion(RelacionModelo relacion) {
        return new RelacionIntermedia(
                relacion.getNombre(),
                normalizador.clase(relacion.getEntidadOrigen().getNombre()),
                normalizador.clase(relacion.getEntidadDestino().getNombre()),
                relacion.getCardinalidadOrigen().name(),
                relacion.getCardinalidadDestino().name()
        );
    }

    private String tipoJava(TipoDato tipoDato) {
        return switch (tipoDato) {
            case TEXTO -> "String";
            case ENTERO -> "Integer";
            case ENTERO_LARGO -> "Long";
            case DECIMAL -> "BigDecimal";
            case BOOLEANO -> "Boolean";
            case FECHA -> "LocalDate";
            case FECHA_HORA -> "LocalDateTime";
        };
    }
}
