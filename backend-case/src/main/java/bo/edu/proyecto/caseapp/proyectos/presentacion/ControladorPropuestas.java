package bo.edu.proyecto.caseapp.proyectos.presentacion;

import bo.edu.proyecto.caseapp.compartido.dominio.ReglaNegocioException;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioPropuestas;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioPropuestas.PropuestaModelo;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioPropuestas.RevisionPropuesta;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioPropuestas.SolicitudDecision;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioPropuestas.SolicitudPropuesta;
import bo.edu.proyecto.caseapp.proyectos.aplicacion.ServicioPropuestas.SolicitudPropuestaQwen;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/projects/{projectId}/proposals")
public class ControladorPropuestas {
    private static final long MAX_AUDIO_BYTES = 25L * 1024L * 1024L;
    private final ServicioPropuestas servicioPropuestas;
    private final HttpClient httpClient;
    private final boolean qwenPhotoEnabled;

    public ControladorPropuestas(ServicioPropuestas servicioPropuestas, @Value("${caseapp.qwen.photo.enabled:false}") boolean qwenPhotoEnabled) {
        this.servicioPropuestas = servicioPropuestas;
        this.qwenPhotoEnabled = qwenPhotoEnabled;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    }

    @PostMapping
    public ResponseEntity<PropuestaModelo> crear(@PathVariable Long projectId, @Valid @RequestBody(required = false) SolicitudPropuesta solicitud) {
        PropuestaModelo propuesta = servicioPropuestas.crear(projectId, solicitud);
        return ResponseEntity.created(URI.create("/projects/" + projectId + "/proposals/" + propuesta.id())).body(propuesta);
    }

    @GetMapping
    public List<PropuestaModelo> listar(@PathVariable Long projectId) {
        return servicioPropuestas.listar(projectId);
    }

    @GetMapping("/{proposalId}")
    public PropuestaModelo obtener(@PathVariable Long projectId, @PathVariable Long proposalId) {
        return servicioPropuestas.obtener(projectId, proposalId);
    }

    @GetMapping("/{proposalId}/review")
    public RevisionPropuesta revision(@PathVariable Long projectId, @PathVariable Long proposalId) {
        return servicioPropuestas.revision(projectId, proposalId);
    }

    @PostMapping("/{proposalId}/review-decision")
    public PropuestaModelo decidir(@PathVariable Long projectId, @PathVariable Long proposalId, @RequestBody(required = false) SolicitudDecision solicitud) {
        return servicioPropuestas.decidir(projectId, proposalId, solicitud);
    }

    @PostMapping("/ai-text/qwen")
    public ResponseEntity<PropuestaModelo> qwenTexto(@PathVariable Long projectId, @RequestBody(required = false) SolicitudPropuestaQwen solicitud) {
        PropuestaModelo propuesta = servicioPropuestas.crearQwenTexto(projectId, solicitud);
        HttpStatus status = propuesta.sourceStatus().equals("READY_FOR_REVIEW") ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(propuesta);
    }

    @PostMapping("/asr-local")
    public ResponseEntity<PropuestaModelo> asrLocal(@PathVariable Long projectId, @RequestParam("audio") MultipartFile audio) {
        if (audio == null || audio.isEmpty()) {
            throw new ReglaNegocioException("Archivo de audio requerido para ASR local");
        }
        if (audio.getSize() > MAX_AUDIO_BYTES) {
            throw new ReglaNegocioException("Audio excede 25 MiB");
        }

        byte[] audioBytes;
        try {
            audioBytes = audio.getBytes();
        } catch (IOException error) {
            return respuestaAsrNoDisponible(projectId, error.getClass().getSimpleName());
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8765/transcribe?language=es-ES"))
                .timeout(Duration.ofSeconds(8))
                .header("Content-Type", audio.getContentType() == null ? "application/octet-stream" : audio.getContentType())
                .POST(HttpRequest.BodyPublishers.ofByteArray(audioBytes))
                .build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException error) {
            return respuestaAsrNoDisponible(projectId, error.getClass().getSimpleName());
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            return respuestaAsrNoDisponible(projectId, error.getClass().getSimpleName());
        }
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(servicioPropuestas.crear(projectId, new SolicitudPropuesta(
                    "ASR local no disponible",
                    "No se pudo transcribir el audio; la propuesta queda como traza sin mutar el modelo.",
                    "ASR_LOCAL",
                    "ASR_UNAVAILABLE",
                    Map.of("endpoint", "http://127.0.0.1:8765/transcribe", "language", "es-ES", "statusCode", response.statusCode())
            )));
        }
        PropuestaModelo propuesta = servicioPropuestas.crear(projectId, new SolicitudPropuesta(
                "Propuesta desde voz",
                response.body(),
                "ASR_LOCAL",
                "READY_FOR_REVIEW",
                Map.of("endpoint", "http://127.0.0.1:8765/transcribe", "language", "es-ES", "maxBytes", MAX_AUDIO_BYTES)
        ));
        return ResponseEntity.created(URI.create("/projects/" + projectId + "/proposals/" + propuesta.id())).body(propuesta);
    }

    private ResponseEntity<PropuestaModelo> respuestaAsrNoDisponible(Long projectId, String error) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(servicioPropuestas.crear(projectId, new SolicitudPropuesta(
                "ASR local no disponible",
                "No se pudo conectar con ASR local; la propuesta queda como traza sin mutar el modelo.",
                "ASR_LOCAL",
                "ASR_UNAVAILABLE",
                Map.of("endpoint", "http://127.0.0.1:8765/transcribe", "language", "es-ES", "error", error)
        )));
    }

    @PostMapping("/photo")
    public ResponseEntity<PropuestaModelo> foto(@PathVariable Long projectId, @RequestParam(value = "photo", required = false) MultipartFile photo) {
        String estado = qwenPhotoEnabled ? "QWEN_OPT_IN_PENDING" : "QWEN_UNCONFIGURED";
        String texto = qwenPhotoEnabled
                ? "Foto recibida como opt-in pendiente: no se invoca Qwen en esta capa demo."
                : "Integracion Qwen/foto deshabilitada o no configurada; propuesta placeholder trazable sin mutar modelo.";
        String fileName = photo == null || photo.getOriginalFilename() == null || photo.getOriginalFilename().isBlank()
                ? "sin-archivo"
                : photo.getOriginalFilename();
        PropuestaModelo propuesta = servicioPropuestas.crear(projectId, new SolicitudPropuesta(
                "Propuesta desde foto",
                texto,
                "PHOTO_QWEN",
                estado,
                Map.of(
                        "qwenPhotoEnabled", qwenPhotoEnabled,
                        "fileName", fileName,
                        "reviewRequired", true
                )
        ));
        return ResponseEntity.status(qwenPhotoEnabled ? HttpStatus.ACCEPTED : HttpStatus.OK).body(propuesta);
    }
}
