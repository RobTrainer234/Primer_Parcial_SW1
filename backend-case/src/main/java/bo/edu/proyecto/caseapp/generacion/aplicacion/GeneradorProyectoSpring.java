package bo.edu.proyecto.caseapp.generacion.aplicacion;

import bo.edu.proyecto.caseapp.generacion.dominio.ModeloIntermedio;
import bo.edu.proyecto.caseapp.generacion.dominio.ModeloIntermedio.AtributoIntermedio;
import bo.edu.proyecto.caseapp.generacion.dominio.ModeloIntermedio.EntidadIntermedia;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Component;

@Component
public class GeneradorProyectoSpring {

    public void generar(ModeloIntermedio modelo, Path directorioSalida) throws IOException {
        String raizJava = modelo.paqueteBase().replace('.', '/');
        escribir(directorioSalida.resolve("pom.xml"), pom(modelo));
        escribir(directorioSalida.resolve("README.md"), readme(modelo));
        escribir(directorioSalida.resolve("src/main/resources/application.yml"), configuracion());
        escribir(directorioSalida.resolve("src/main/java/" + raizJava + "/AplicacionGenerada.java"), aplicacion(modelo));
        escribir(directorioSalida.resolve("src/main/java/" + raizJava + "/excepcion/RecursoNoEncontradoException.java"), excepcion(modelo));
        escribir(directorioSalida.resolve("src/main/java/" + raizJava + "/excepcion/ManejadorErrores.java"), manejadorErrores(modelo));
        for (EntidadIntermedia entidad : modelo.entidades()) {
            escribir(directorioSalida.resolve("src/main/java/" + raizJava + "/entidad/" + entidad.nombreClase() + ".java"), entidad(modelo, entidad));
            escribir(directorioSalida.resolve("src/main/java/" + raizJava + "/dto/" + entidad.nombreClase() + "Solicitud.java"), dtoSolicitud(modelo, entidad));
            escribir(directorioSalida.resolve("src/main/java/" + raizJava + "/dto/" + entidad.nombreClase() + "Respuesta.java"), dtoRespuesta(modelo, entidad));
            escribir(directorioSalida.resolve("src/main/java/" + raizJava + "/repositorio/Repositorio" + entidad.nombreClase() + ".java"), repositorio(modelo, entidad));
            escribir(directorioSalida.resolve("src/main/java/" + raizJava + "/servicio/Servicio" + entidad.nombreClase() + ".java"), servicio(modelo, entidad));
            escribir(directorioSalida.resolve("src/main/java/" + raizJava + "/controlador/Controlador" + entidad.nombreClase() + ".java"), controlador(modelo, entidad));
        }
    }

    private void escribir(Path ruta, String contenido) throws IOException {
        Files.createDirectories(ruta.getParent());
        Files.writeString(ruta, contenido, StandardCharsets.UTF_8);
    }

    private String pom(ModeloIntermedio modelo) {
        return """
                <?xml version=\"1.0\" encoding=\"UTF-8\"?>
                <project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">
                    <modelVersion>4.0.0</modelVersion>
                    <parent>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-parent</artifactId>
                        <version>3.3.5</version>
                        <relativePath/>
                    </parent>
                    <groupId>%s</groupId>
                    <artifactId>%s</artifactId>
                    <version>0.1.0-SNAPSHOT</version>
                    <properties><java.version>21</java.version></properties>
                    <dependencies>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-validation</artifactId></dependency>
                        <dependency><groupId>org.postgresql</groupId><artifactId>postgresql</artifactId><scope>runtime</scope></dependency>
                        <dependency><groupId>org.springdoc</groupId><artifactId>springdoc-openapi-starter-webmvc-ui</artifactId><version>2.6.0</version></dependency>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-test</artifactId><scope>test</scope></dependency>
                    </dependencies>
                    <build><plugins><plugin><groupId>org.springframework.boot</groupId><artifactId>spring-boot-maven-plugin</artifactId></plugin></plugins></build>
                </project>
                """.formatted(modelo.paqueteBase(), modelo.nombreAplicacion().toLowerCase().replaceAll("[^a-z0-9-]", "-"));
    }

    private String readme(ModeloIntermedio modelo) {
        return """
                # Backend generado: %s

                Proyecto Spring Boot generado automaticamente desde la herramienta CASE.

                ## Ejecutar

                ```bash
                mvn spring-boot:run
                ```

                ## Swagger

                ```text
                http://localhost:8081/swagger-ui.html
                ```

                ## Entidades generadas

                %s
                """.formatted(modelo.nombreAplicacion(), modelo.entidades().stream().map(e -> "- " + e.nombreClase()).reduce("", (a, b) -> a + b + "\n"));
    }

    private String configuracion() {
        return """
                server:
                  port: ${PUERTO_BACKEND_GENERADO:8081}
                spring:
                  datasource:
                    url: ${BASE_DATOS_URL:jdbc:postgresql://localhost:5432/backend_generado}
                    username: ${BASE_DATOS_USUARIO:generado_user}
                    password: ${BASE_DATOS_CLAVE:generado_password}
                  jpa:
                    hibernate:
                      ddl-auto: update
                    open-in-view: false
                springdoc:
                  swagger-ui:
                    path: /swagger-ui.html
                """;
    }

    private String aplicacion(ModeloIntermedio modelo) {
        return """
                package %s;

                import org.springframework.boot.SpringApplication;
                import org.springframework.boot.autoconfigure.SpringBootApplication;

                @SpringBootApplication
                public class AplicacionGenerada {
                    public static void main(String[] argumentos) {
                        SpringApplication.run(AplicacionGenerada.class, argumentos);
                    }
                }
                """.formatted(modelo.paqueteBase());
    }

    private String entidad(ModeloIntermedio modelo, EntidadIntermedia entidad) {
        StringBuilder imports = new StringBuilder("""
                package %s.entidad;

                import jakarta.persistence.Column;
                import jakarta.persistence.Entity;
                import jakarta.persistence.GeneratedValue;
                import jakarta.persistence.GenerationType;
                import jakarta.persistence.Id;
                import jakarta.persistence.Table;
                """.formatted(modelo.paqueteBase()));
        if (entidad.atributos().stream().anyMatch(a -> a.tipoJava().equals("BigDecimal"))) imports.append("import java.math.BigDecimal;\n");
        if (entidad.atributos().stream().anyMatch(a -> a.tipoJava().equals("LocalDate"))) imports.append("import java.time.LocalDate;\n");
        if (entidad.atributos().stream().anyMatch(a -> a.tipoJava().equals("LocalDateTime"))) imports.append("import java.time.LocalDateTime;\n");
        StringBuilder campos = new StringBuilder();
        StringBuilder metodos = new StringBuilder();
        for (AtributoIntermedio atributo : entidad.atributos()) {
            if (atributo.clavePrimaria()) {
                campos.append("    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
            }
            campos.append("    @Column(nullable = ").append(!atributo.obligatorio() && !atributo.clavePrimaria()).append(", unique = ").append(atributo.valorUnico()).append(")\n");
            campos.append("    private ").append(atributo.tipoJava()).append(" ").append(atributo.nombreCampo()).append(";\n\n");
            metodos.append("    public ").append(atributo.tipoJava()).append(" get").append(atributo.nombreMetodo()).append("() { return ").append(atributo.nombreCampo()).append("; }\n");
            metodos.append("    public void set").append(atributo.nombreMetodo()).append("(").append(atributo.tipoJava()).append(" ").append(atributo.nombreCampo()).append(") { this.").append(atributo.nombreCampo()).append(" = ").append(atributo.nombreCampo()).append("; }\n\n");
        }
        return imports + "\n@Entity\n@Table(name = \"" + entidad.rutaRecurso().replace('-', '_') + "\")\npublic class " + entidad.nombreClase() + " {\n\n" + campos + metodos + "}\n";
    }

    private String dtoSolicitud(ModeloIntermedio modelo, EntidadIntermedia entidad) {
        return dto(modelo, entidad, entidad.nombreClase() + "Solicitud", false);
    }

    private String dtoRespuesta(ModeloIntermedio modelo, EntidadIntermedia entidad) {
        return dto(modelo, entidad, entidad.nombreClase() + "Respuesta", true);
    }

    private String dto(ModeloIntermedio modelo, EntidadIntermedia entidad, String nombreDto, boolean incluirClave) {
        StringBuilder imports = new StringBuilder("package " + modelo.paqueteBase() + ".dto;\n\n");
        if (entidad.atributos().stream().anyMatch(a -> a.tipoJava().equals("BigDecimal"))) imports.append("import java.math.BigDecimal;\n");
        if (entidad.atributos().stream().anyMatch(a -> a.tipoJava().equals("LocalDate"))) imports.append("import java.time.LocalDate;\n");
        if (entidad.atributos().stream().anyMatch(a -> a.tipoJava().equals("LocalDateTime"))) imports.append("import java.time.LocalDateTime;\n");
        String campos = entidad.atributos().stream()
                .filter(a -> incluirClave || !a.clavePrimaria())
                .map(a -> "        " + a.tipoJava() + " " + a.nombreCampo())
                .reduce((a, b) -> a + ",\n" + b)
                .orElse("");
        if (campos.isBlank()) {
            return imports + "\npublic record " + nombreDto + "() {\n}\n";
        }
        return imports + "\npublic record " + nombreDto + "(\n" + campos + "\n) {\n}\n";
    }

    private String repositorio(ModeloIntermedio modelo, EntidadIntermedia entidad) {
        return """
                package %s.repositorio;

                import %s.entidad.%s;
                import org.springframework.data.jpa.repository.JpaRepository;

                public interface Repositorio%s extends JpaRepository<%s, %s> {
                }
                """.formatted(modelo.paqueteBase(), modelo.paqueteBase(), entidad.nombreClase(), entidad.nombreClase(), entidad.nombreClase(), entidad.clavePrimaria().tipoJava());
    }

    private String servicio(ModeloIntermedio modelo, EntidadIntermedia entidad) {
        AtributoIntermedio clave = entidad.clavePrimaria();
        return """
                package %s.servicio;

                import %s.dto.%sSolicitud;
                import %s.dto.%sRespuesta;
                import %s.entidad.%s;
                import %s.excepcion.RecursoNoEncontradoException;
                import %s.repositorio.Repositorio%s;
                import java.util.List;
                import org.springframework.stereotype.Service;
                import org.springframework.transaction.annotation.Transactional;

                @Service
                public class Servicio%s {
                    private final Repositorio%s repositorio;

                    public Servicio%s(Repositorio%s repositorio) {
                        this.repositorio = repositorio;
                    }

                    @Transactional(readOnly = true)
                    public List<%sRespuesta> listar() {
                        return repositorio.findAll().stream().map(this::convertir).toList();
                    }

                    @Transactional(readOnly = true)
                    public %sRespuesta obtener(%s id) {
                        return convertir(buscar(id));
                    }

                    @Transactional
                    public %sRespuesta crear(%sSolicitud solicitud) {
                        %s entidad = new %s();
                %s
                        return convertir(repositorio.save(entidad));
                    }

                    @Transactional
                    public %sRespuesta actualizar(%s id, %sSolicitud solicitud) {
                        %s entidad = buscar(id);
                %s
                        return convertir(entidad);
                    }

                    @Transactional
                    public void eliminar(%s id) {
                        repositorio.delete(buscar(id));
                    }

                    private %s buscar(%s id) {
                        return repositorio.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("%s no encontrado: " + id));
                    }

                    private %sRespuesta convertir(%s entidad) {
                        return new %sRespuesta(%s);
                    }
                }
                """.formatted(
                modelo.paqueteBase(), modelo.paqueteBase(), entidad.nombreClase(), modelo.paqueteBase(), entidad.nombreClase(), modelo.paqueteBase(), entidad.nombreClase(), modelo.paqueteBase(), modelo.paqueteBase(), entidad.nombreClase(),
                entidad.nombreClase(), entidad.nombreClase(), entidad.nombreClase(), entidad.nombreClase(), entidad.nombreClase(),
                entidad.nombreClase(), clave.tipoJava(), entidad.nombreClase(), entidad.nombreClase(), entidad.nombreClase(), entidad.nombreClase(), setters(entidad, false),
                entidad.nombreClase(), clave.tipoJava(), entidad.nombreClase(), entidad.nombreClase(), setters(entidad, false),
                clave.tipoJava(), entidad.nombreClase(), clave.tipoJava(), entidad.nombreClase(), entidad.nombreClase(), entidad.nombreClase(), entidad.nombreClase(), argumentosRespuesta(entidad)
        );
    }

    private String controlador(ModeloIntermedio modelo, EntidadIntermedia entidad) {
        AtributoIntermedio clave = entidad.clavePrimaria();
        return """
                package %s.controlador;

                import %s.dto.%sSolicitud;
                import %s.dto.%sRespuesta;
                import %s.servicio.Servicio%s;
                import java.net.URI;
                import java.util.List;
                import org.springframework.http.ResponseEntity;
                import org.springframework.web.bind.annotation.DeleteMapping;
                import org.springframework.web.bind.annotation.GetMapping;
                import org.springframework.web.bind.annotation.PathVariable;
                import org.springframework.web.bind.annotation.PostMapping;
                import org.springframework.web.bind.annotation.PutMapping;
                import org.springframework.web.bind.annotation.RequestBody;
                import org.springframework.web.bind.annotation.RequestMapping;
                import org.springframework.web.bind.annotation.RestController;

                @RestController
                @RequestMapping("/%s")
                public class Controlador%s {
                    private final Servicio%s servicio;

                    public Controlador%s(Servicio%s servicio) {
                        this.servicio = servicio;
                    }

                    @GetMapping
                    public List<%sRespuesta> listar() { return servicio.listar(); }

                    @GetMapping("/{id}")
                    public %sRespuesta obtener(@PathVariable %s id) { return servicio.obtener(id); }

                    @PostMapping
                    public ResponseEntity<%sRespuesta> crear(@RequestBody %sSolicitud solicitud) {
                        %sRespuesta respuesta = servicio.crear(solicitud);
                        return ResponseEntity.created(URI.create("/%s")).body(respuesta);
                    }

                    @PutMapping("/{id}")
                    public %sRespuesta actualizar(@PathVariable %s id, @RequestBody %sSolicitud solicitud) { return servicio.actualizar(id, solicitud); }

                    @DeleteMapping("/{id}")
                    public ResponseEntity<Void> eliminar(@PathVariable %s id) {
                        servicio.eliminar(id);
                        return ResponseEntity.noContent().build();
                    }
                }
                """.formatted(modelo.paqueteBase(), modelo.paqueteBase(), entidad.nombreClase(), modelo.paqueteBase(), entidad.nombreClase(), modelo.paqueteBase(), entidad.nombreClase(), entidad.rutaRecurso(), entidad.nombreClase(), entidad.nombreClase(), entidad.nombreClase(), entidad.nombreClase(), entidad.nombreClase(), entidad.nombreClase(), entidad.nombreClase(), clave.tipoJava(), entidad.nombreClase(), entidad.nombreClase(), entidad.nombreClase(), entidad.rutaRecurso(), entidad.nombreClase(), clave.tipoJava(), entidad.nombreClase(), clave.tipoJava());
    }

    private String excepcion(ModeloIntermedio modelo) {
        return """
                package %s.excepcion;

                public class RecursoNoEncontradoException extends RuntimeException {
                    public RecursoNoEncontradoException(String mensaje) { super(mensaje); }
                }
                """.formatted(modelo.paqueteBase());
    }

    private String manejadorErrores(ModeloIntermedio modelo) {
        return """
                package %s.excepcion;

                import java.time.LocalDateTime;
                import java.util.Map;
                import org.springframework.http.HttpStatus;
                import org.springframework.http.ResponseEntity;
                import org.springframework.web.bind.annotation.ExceptionHandler;
                import org.springframework.web.bind.annotation.RestControllerAdvice;

                @RestControllerAdvice
                public class ManejadorErrores {
                    @ExceptionHandler(RecursoNoEncontradoException.class)
                    ResponseEntity<Map<String, Object>> manejar(RecursoNoEncontradoException error) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                            "fecha", LocalDateTime.now().toString(),
                            "mensaje", error.getMessage()
                        ));
                    }
                }
                """.formatted(modelo.paqueteBase());
    }

    private String setters(EntidadIntermedia entidad, boolean incluirClave) {
        StringBuilder resultado = new StringBuilder();
        for (AtributoIntermedio atributo : entidad.atributos()) {
            if (incluirClave || !atributo.clavePrimaria()) {
                resultado.append("        entidad.set").append(atributo.nombreMetodo()).append("(solicitud.").append(atributo.nombreCampo()).append("());\n");
            }
        }
        return resultado.toString();
    }

    private String argumentosRespuesta(EntidadIntermedia entidad) {
        return entidad.atributos().stream()
                .map(a -> "entidad.get" + a.nombreMetodo() + "()")
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }
}
