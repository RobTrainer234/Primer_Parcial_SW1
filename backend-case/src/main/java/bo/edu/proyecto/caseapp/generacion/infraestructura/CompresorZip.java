package bo.edu.proyecto.caseapp.generacion.infraestructura;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.stereotype.Component;

@Component
public class CompresorZip {

    public void comprimir(Path directorioOrigen, Path archivoDestino) throws IOException {
        Files.createDirectories(archivoDestino.getParent());
        try (OutputStream salida = Files.newOutputStream(archivoDestino);
             ZipOutputStream zip = new ZipOutputStream(salida)) {
            try (var rutas = Files.walk(directorioOrigen)) {
                for (Path ruta : rutas.filter(Files::isRegularFile).toList()) {
                    Path relativa = directorioOrigen.relativize(ruta);
                    zip.putNextEntry(new ZipEntry(relativa.toString().replace('\\', '/')));
                    Files.copy(ruta, zip);
                    zip.closeEntry();
                }
            }
        }
    }
}
