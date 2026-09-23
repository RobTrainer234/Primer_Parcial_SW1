package bo.edu.proyecto.caseapp.validacion.aplicacion;

import bo.edu.proyecto.caseapp.modelado.aplicacion.ServicioModelado;
import bo.edu.proyecto.caseapp.modelado.dominio.AtributoEntidad;
import bo.edu.proyecto.caseapp.modelado.dominio.EntidadModelo;
import bo.edu.proyecto.caseapp.modelado.dominio.ModeloConceptual;
import bo.edu.proyecto.caseapp.modelado.dominio.RelacionModelo;
import bo.edu.proyecto.caseapp.modelado.dominio.TipoDato;
import bo.edu.proyecto.caseapp.validacion.dominio.ErrorValidacion;
import bo.edu.proyecto.caseapp.validacion.dominio.ResultadoValidacion;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioValidacionModelo {

    private static final int LONGITUD_MAXIMA_NOMBRE = 120;
    private static final Set<TipoDato> TIPOS_DATO_SOPORTADOS = EnumSet.allOf(TipoDato.class);

    private final ServicioModelado servicioModelado;

    public ServicioValidacionModelo(ServicioModelado servicioModelado) {
        this.servicioModelado = servicioModelado;
    }

    @Transactional(readOnly = true)
    public ResultadoValidacion validar(Long modeloId) {
        ModeloConceptual modelo = servicioModelado.obtenerModelo(modeloId);
        List<ErrorValidacion> errores = new ArrayList<>();
        validarEntidades(modelo, errores);
        validarRelaciones(modelo, errores);
        return ResultadoValidacion.desde(errores);
    }

    private void validarEntidades(ModeloConceptual modelo, List<ErrorValidacion> errores) {
        if (modelo.getEntidades().isEmpty()) {
            errores.add(new ErrorValidacion("MODELO_SIN_ENTIDADES", "El modelo debe tener al menos una clase para validar el subconjunto UML 2.5 de diagramas de clases.", modelo.getNombre()));
        }

        Set<String> nombresEntidades = new HashSet<>();
        for (EntidadModelo entidad : modelo.getEntidades()) {
            String nombreEntidad = normalizar(entidad.getNombre());
            if (nombreEntidad.isBlank()) {
                errores.add(new ErrorValidacion("UML25-CLASS-001", "La clase debe tener un nombre.", String.valueOf(entidad.getId())));
            } else {
                if (entidad.getNombre().trim().length() > LONGITUD_MAXIMA_NOMBRE) {
                    errores.add(new ErrorValidacion("UML25-CLASS-001", "El nombre de la clase no debe superar 120 caracteres: " + entidad.getNombre(), entidad.getNombre()));
                }
                if (!nombresEntidades.add(nombreEntidad)) {
                    errores.add(new ErrorValidacion("UML25-MODEL-001", "El modelo no puede tener clases con el mismo nombre: " + entidad.getNombre(), entidad.getNombre()));
                }
                if (!esIdentificadorValido(entidad.getNombre())) {
                    errores.add(new ErrorValidacion("UML25-CLASS-002", "El nombre de la clase debe ser un identificador compatible con Java: " + entidad.getNombre(), entidad.getNombre()));
                }
            }
            validarAtributos(entidad, errores);
        }
    }

    private void validarAtributos(EntidadModelo entidad, List<ErrorValidacion> errores) {
        if (entidad.getAtributos().isEmpty()) {
            errores.add(new ErrorValidacion("ENTIDAD_SIN_ATRIBUTOS", "La clase debe tener al menos un atributo: " + entidad.getNombre(), entidad.getNombre()));
        }
        boolean tieneClavePrimaria = false;
        Set<String> nombresAtributos = new HashSet<>();
        for (AtributoEntidad atributo : entidad.getAtributos()) {
            String nombreAtributo = normalizar(atributo.getNombre());
            if (nombreAtributo.isBlank()) {
                errores.add(new ErrorValidacion("UML25-PROP-001", "Cada atributo debe tener nombre en la clase " + entidad.getNombre() + ".", entidad.getNombre()));
            } else {
                if (!nombresAtributos.add(nombreAtributo)) {
                    errores.add(new ErrorValidacion("UML25-PROP-002", "La clase " + entidad.getNombre() + " no puede tener atributos duplicados: " + atributo.getNombre(), entidad.getNombre()));
                }
                if (atributo.getNombre().trim().length() > LONGITUD_MAXIMA_NOMBRE) {
                    errores.add(new ErrorValidacion("UML25-PROP-001", "El nombre del atributo no debe superar 120 caracteres: " + atributo.getNombre(), entidad.getNombre() + "." + atributo.getNombre()));
                }
                if (!esIdentificadorValido(atributo.getNombre())) {
                    errores.add(new ErrorValidacion("UML25-PROP-001", "El nombre del atributo debe ser un identificador compatible con Java: " + atributo.getNombre(), entidad.getNombre() + "." + atributo.getNombre()));
                }
            }
            if (atributo.getTipoDato() == null || !TIPOS_DATO_SOPORTADOS.contains(atributo.getTipoDato())) {
                errores.add(new ErrorValidacion("UML25-PROP-001", "El atributo " + atributo.getNombre() + " debe declarar un tipo de dato soportado por el perfil.", entidad.getNombre() + "." + atributo.getNombre()));
            }
            if (atributo.isClavePrimaria()) {
                tieneClavePrimaria = true;
            }
        }
        if (!tieneClavePrimaria) {
            errores.add(new ErrorValidacion("UML25-PROP-003", "La clase debe declarar una clave primaria: " + entidad.getNombre(), entidad.getNombre()));
        }
    }

    private void validarRelaciones(ModeloConceptual modelo, List<ErrorValidacion> errores) {
        Set<Long> entidadesDelModelo = new HashSet<>();
        for (EntidadModelo entidad : modelo.getEntidades()) {
            entidadesDelModelo.add(entidad.getId());
        }
        for (RelacionModelo relacion : modelo.getRelaciones()) {
            boolean extremosPresentes = relacion.getEntidadOrigen() != null && relacion.getEntidadDestino() != null;
            boolean extremosDelModelo = extremosPresentes
                    && entidadesDelModelo.contains(relacion.getEntidadOrigen().getId())
                    && entidadesDelModelo.contains(relacion.getEntidadDestino().getId());

            if (!extremosDelModelo) {
                errores.add(new ErrorValidacion("UML25-ASSOC-001", "La relación debe conectar clases que pertenecen al modelo.", relacion.getNombre()));
            }
            if (relacion.getCardinalidadOrigen() == null || relacion.getCardinalidadDestino() == null) {
                errores.add(new ErrorValidacion("UML25-ASSOC-002", "La relación debe declarar multiplicidad en ambos extremos.", relacion.getNombre()));
            }
            if (esBlanco(relacion.getNombre()) && esBlanco(relacion.getVerbo())) {
                errores.add(new ErrorValidacion("UML25-ASSOC-003", "La relación debe tener nombre o verbo/rol semántico; el verbo es opcional si existe nombre.", String.valueOf(relacion.getId())));
            }
            if (extremosDelModelo && relacion.getEntidadOrigen().getId().equals(relacion.getEntidadDestino().getId())) {
                errores.add(new ErrorValidacion("UML25-ASSOC-004", "El perfil actual no permite relaciones de una clase consigo misma: " + relacion.getEntidadOrigen().getNombre(), relacion.getNombre()));
            }
        }
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toLowerCase(Locale.ROOT);
    }

    private boolean esBlanco(String valor) {
        return valor == null || valor.trim().isBlank();
    }

    private boolean esIdentificadorValido(String valor) {
        return valor != null && valor.matches("[A-Za-z_][A-Za-z0-9_]*");
    }
}
