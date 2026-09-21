package bo.edu.proyecto.caseapp.validacion.aplicacion;

import bo.edu.proyecto.caseapp.modelado.aplicacion.ServicioModelado;
import bo.edu.proyecto.caseapp.modelado.dominio.AtributoEntidad;
import bo.edu.proyecto.caseapp.modelado.dominio.EntidadModelo;
import bo.edu.proyecto.caseapp.modelado.dominio.ModeloConceptual;
import bo.edu.proyecto.caseapp.modelado.dominio.RelacionModelo;
import bo.edu.proyecto.caseapp.validacion.dominio.ErrorValidacion;
import bo.edu.proyecto.caseapp.validacion.dominio.ResultadoValidacion;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioValidacionModelo {

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
            errores.add(new ErrorValidacion("MODELO_SIN_ENTIDADES", "El modelo debe tener al menos una entidad", modelo.getNombre()));
        }

        Set<String> nombresEntidades = new HashSet<>();
        for (EntidadModelo entidad : modelo.getEntidades()) {
            String nombreEntidad = normalizar(entidad.getNombre());
            if (nombreEntidad.isBlank()) {
                errores.add(new ErrorValidacion("ENTIDAD_SIN_NOMBRE", "Existe una entidad sin nombre", String.valueOf(entidad.getId())));
            }
            if (!nombresEntidades.add(nombreEntidad)) {
                errores.add(new ErrorValidacion("ENTIDAD_DUPLICADA", "Existe una entidad duplicada: " + entidad.getNombre(), entidad.getNombre()));
            }
            if (!esIdentificadorValido(entidad.getNombre())) {
                errores.add(new ErrorValidacion("NOMBRE_ENTIDAD_INVALIDO", "El nombre de entidad no es compatible con Java: " + entidad.getNombre(), entidad.getNombre()));
            }
            validarAtributos(entidad, errores);
        }
    }

    private void validarAtributos(EntidadModelo entidad, List<ErrorValidacion> errores) {
        if (entidad.getAtributos().isEmpty()) {
            errores.add(new ErrorValidacion("ENTIDAD_SIN_ATRIBUTOS", "La entidad no tiene atributos: " + entidad.getNombre(), entidad.getNombre()));
        }
        boolean tieneClavePrimaria = false;
        Set<String> nombresAtributos = new HashSet<>();
        for (AtributoEntidad atributo : entidad.getAtributos()) {
            String nombreAtributo = normalizar(atributo.getNombre());
            if (nombreAtributo.isBlank()) {
                errores.add(new ErrorValidacion("ATRIBUTO_SIN_NOMBRE", "Existe un atributo sin nombre en " + entidad.getNombre(), entidad.getNombre()));
            }
            if (!nombresAtributos.add(nombreAtributo)) {
                errores.add(new ErrorValidacion("ATRIBUTO_DUPLICADO", "Atributo duplicado en " + entidad.getNombre() + ": " + atributo.getNombre(), entidad.getNombre()));
            }
            if (!esIdentificadorValido(atributo.getNombre())) {
                errores.add(new ErrorValidacion("NOMBRE_ATRIBUTO_INVALIDO", "El nombre de atributo no es compatible con Java: " + atributo.getNombre(), entidad.getNombre() + "." + atributo.getNombre()));
            }
            if (atributo.isClavePrimaria()) {
                tieneClavePrimaria = true;
            }
        }
        if (!tieneClavePrimaria) {
            errores.add(new ErrorValidacion("ENTIDAD_SIN_CLAVE_PRIMARIA", "La entidad no tiene clave primaria: " + entidad.getNombre(), entidad.getNombre()));
        }
    }

    private void validarRelaciones(ModeloConceptual modelo, List<ErrorValidacion> errores) {
        Set<Long> entidadesDelModelo = new HashSet<>();
        for (EntidadModelo entidad : modelo.getEntidades()) {
            entidadesDelModelo.add(entidad.getId());
        }
        for (RelacionModelo relacion : modelo.getRelaciones()) {
            if (relacion.getEntidadOrigen() == null || relacion.getEntidadDestino() == null) {
                errores.add(new ErrorValidacion("RELACION_INCOMPLETA", "Existe una relacion incompleta", relacion.getNombre()));
                continue;
            }
            if (!entidadesDelModelo.contains(relacion.getEntidadOrigen().getId()) || !entidadesDelModelo.contains(relacion.getEntidadDestino().getId())) {
                errores.add(new ErrorValidacion("RELACION_CON_REFERENCIA_INVALIDA", "La relacion referencia entidades fuera del modelo", relacion.getNombre()));
            }
        }
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toLowerCase(Locale.ROOT);
    }

    private boolean esIdentificadorValido(String valor) {
        return valor != null && valor.matches("[A-Za-z_][A-Za-z0-9_]*");
    }
}
