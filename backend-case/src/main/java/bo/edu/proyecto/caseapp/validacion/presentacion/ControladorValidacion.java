package bo.edu.proyecto.caseapp.validacion.presentacion;

import bo.edu.proyecto.caseapp.validacion.aplicacion.ServicioValidacionModelo;
import bo.edu.proyecto.caseapp.validacion.dominio.ResultadoValidacion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/modelos/{modeloId}/validacion")
public class ControladorValidacion {

    private final ServicioValidacionModelo servicioValidacionModelo;

    public ControladorValidacion(ServicioValidacionModelo servicioValidacionModelo) {
        this.servicioValidacionModelo = servicioValidacionModelo;
    }

    @PostMapping
    public ResultadoValidacion validar(@PathVariable Long modeloId) {
        return servicioValidacionModelo.validar(modeloId);
    }

    @GetMapping
    public ResultadoValidacion consultar(@PathVariable Long modeloId) {
        return servicioValidacionModelo.validar(modeloId);
    }
}
