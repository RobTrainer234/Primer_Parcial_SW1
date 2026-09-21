package bo.edu.proyecto.caseapp.compartido.presentacion;

import bo.edu.proyecto.caseapp.compartido.dominio.RecursoNoEncontradoException;
import bo.edu.proyecto.caseapp.compartido.dominio.ReglaNegocioException;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    ResponseEntity<Map<String, Object>> manejarNoEncontrado(RecursoNoEncontradoException error) {
        return respuesta(HttpStatus.NOT_FOUND, error.getMessage());
    }

    @ExceptionHandler({ReglaNegocioException.class, ConstraintViolationException.class})
    ResponseEntity<Map<String, Object>> manejarRegla(RuntimeException error) {
        return respuesta(HttpStatus.BAD_REQUEST, error.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException error) {
        String mensaje = error.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(campo -> campo.getField() + ": " + campo.getDefaultMessage())
                .orElse("Solicitud invalida");
        return respuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    private ResponseEntity<Map<String, Object>> respuesta(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(Map.of(
                "fecha", LocalDateTime.now().toString(),
                "estado", estado.value(),
                "error", estado.getReasonPhrase(),
                "mensaje", mensaje
        ));
    }
}
