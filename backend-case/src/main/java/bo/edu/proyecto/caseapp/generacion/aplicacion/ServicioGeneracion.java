package bo.edu.proyecto.caseapp.generacion.aplicacion;

import bo.edu.proyecto.caseapp.artefactos.dominio.ArtefactoGenerado;
import bo.edu.proyecto.caseapp.artefactos.infraestructura.RepositorioArtefactoGenerado;
import bo.edu.proyecto.caseapp.compartido.dominio.RecursoNoEncontradoException;
import bo.edu.proyecto.caseapp.compartido.dominio.ReglaNegocioException;
import bo.edu.proyecto.caseapp.generacion.dominio.ModeloIntermedio;
import bo.edu.proyecto.caseapp.generacion.dominio.TrabajoGeneracion;
import bo.edu.proyecto.caseapp.generacion.infraestructura.CompresorZip;
import bo.edu.proyecto.caseapp.generacion.infraestructura.RepositorioTrabajoGeneracion;
import bo.edu.proyecto.caseapp.modelado.aplicacion.ServicioModelado;
import bo.edu.proyecto.caseapp.modelado.dominio.ModeloConceptual;
import bo.edu.proyecto.caseapp.validacion.aplicacion.ServicioValidacionModelo;
import bo.edu.proyecto.caseapp.validacion.dominio.ResultadoValidacion;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioGeneracion {

    private final ServicioModelado servicioModelado;
    private final ServicioValidacionModelo servicioValidacionModelo;
    private final ConstructorModeloIntermedio constructorModeloIntermedio;
    private final GeneradorProyectoSpring generadorProyectoSpring;
    private final CompresorZip compresorZip;
    private final RepositorioTrabajoGeneracion repositorioTrabajo;
    private final RepositorioArtefactoGenerado repositorioArtefacto;
    private final Path directorioArtefactos;

    public ServicioGeneracion(
            ServicioModelado servicioModelado,
            ServicioValidacionModelo servicioValidacionModelo,
            ConstructorModeloIntermedio constructorModeloIntermedio,
            GeneradorProyectoSpring generadorProyectoSpring,
            CompresorZip compresorZip,
            RepositorioTrabajoGeneracion repositorioTrabajo,
            RepositorioArtefactoGenerado repositorioArtefacto,
            @Value("${caseapp.artefactos.directorio:generados}") String directorioArtefactos
    ) {
        this.servicioModelado = servicioModelado;
        this.servicioValidacionModelo = servicioValidacionModelo;
        this.constructorModeloIntermedio = constructorModeloIntermedio;
        this.generadorProyectoSpring = generadorProyectoSpring;
        this.compresorZip = compresorZip;
        this.repositorioTrabajo = repositorioTrabajo;
        this.repositorioArtefacto = repositorioArtefacto;
        this.directorioArtefactos = Path.of(directorioArtefactos);
    }

    @Transactional
    public TrabajoGeneracion generar(Long modeloId) {
        ModeloConceptual modelo = servicioModelado.obtenerModelo(modeloId);
        ResultadoValidacion resultado = servicioValidacionModelo.validar(modeloId);
        TrabajoGeneracion trabajo = repositorioTrabajo.save(new TrabajoGeneracion(modelo));
        if (!resultado.valido()) {
            trabajo.fallar("El modelo tiene errores de validacion y no puede generar backend");
            return trabajo;
        }
        try {
            ModeloIntermedio intermedio = constructorModeloIntermedio.construir(modelo);
            Path temporal = Files.createTempDirectory("backend-generado-" + trabajo.getId());
            Path proyecto = temporal.resolve(intermedio.nombreAplicacion().toLowerCase());
            generadorProyectoSpring.generar(intermedio, proyecto);
            Files.createDirectories(directorioArtefactos);
            String nombreArchivo = intermedio.nombreAplicacion().toLowerCase() + "-" + trabajo.getId() + ".zip";
            Path zip = directorioArtefactos.resolve(nombreArchivo).toAbsolutePath().normalize();
            compresorZip.comprimir(proyecto, zip);
            repositorioArtefacto.save(new ArtefactoGenerado(trabajo, nombreArchivo, zip.toString()));
            trabajo.completar();
            return trabajo;
        } catch (IOException | RuntimeException error) {
            trabajo.fallar(error.getMessage());
            return trabajo;
        }
    }

    @Transactional(readOnly = true)
    public TrabajoGeneracion obtenerTrabajo(Long id) {
        return repositorioTrabajo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Trabajo de generacion no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public ArtefactoGenerado obtenerArtefacto(Long trabajoId) {
        obtenerTrabajo(trabajoId);
        return repositorioArtefacto.findByTrabajoGeneracionId(trabajoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El trabajo no tiene artefacto generado"));
    }

    @Transactional(readOnly = true)
    public Resource descargarArtefacto(Long trabajoId) {
        ArtefactoGenerado artefacto = obtenerArtefacto(trabajoId);
        FileSystemResource recurso = new FileSystemResource(artefacto.getRutaArchivo());
        if (!recurso.exists()) {
            throw new ReglaNegocioException("El archivo del artefacto no existe en disco");
        }
        return recurso;
    }
}
