package bo.edu.proyecto.caseapp.proyectos.aplicacion;

import bo.edu.proyecto.caseapp.configuracion.PropiedadesAiPropuestas;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ClienteQwenPropuestas {
    private final ObjectMapper objectMapper;

    public ClienteQwenPropuestas(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ResultadoQwen solicitar(String textoFuente, PropiedadesAiPropuestas.Qwen qwen) {
        String contexto = limitarUtf8(textoFuente, qwen.getMaxContextBytes());
        Map<String, Object> payload = Map.of(
                "model", qwen.getModel(),
                "messages", List.of(
                        Map.of("role", "system", "content", "You generate concise UML class-model change proposals. Return reviewable text only. Do not claim that the model was changed."),
                        Map.of("role", "user", "content", contexto)
                ),
                "max_tokens", qwen.getMaxCompletionTokens(),
                "temperature", 0.2
        );
        byte[] requestBody;
        try {
            requestBody = objectMapper.writeValueAsBytes(payload);
        } catch (IOException error) {
            return ResultadoQwen.fallo("QWEN_INVALID_REQUEST", "No se pudo serializar la solicitud Qwen", error.getClass().getSimpleName(), null);
        }
        if (requestBody.length > qwen.getMaxRequestBytes()) {
            return ResultadoQwen.fallo("QWEN_REQUEST_TOO_LARGE", "La solicitud Qwen excede el limite configurado", null, null);
        }

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(qwen.getConnectTimeoutMs()))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(qwen.getEndpoint()))
                .timeout(Duration.ofMillis(qwen.getReadTimeoutMs()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestBody))
                .build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException error) {
            return ResultadoQwen.fallo("QWEN_UNAVAILABLE", "No se pudo conectar con Qwen", error.getClass().getSimpleName(), null);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            return ResultadoQwen.fallo("QWEN_INTERRUPTED", "La solicitud Qwen fue interrumpida", error.getClass().getSimpleName(), null);
        }
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return ResultadoQwen.fallo("QWEN_HTTP_ERROR", "Qwen devolvio estado HTTP no exitoso", null, response.statusCode());
        }
        byte[] responseBytes = response.body() == null ? new byte[0] : response.body().getBytes(StandardCharsets.UTF_8);
        if (responseBytes.length > qwen.getMaxResponseBytes()) {
            return ResultadoQwen.fallo("QWEN_RESPONSE_TOO_LARGE", "La respuesta Qwen excede el limite configurado", null, response.statusCode());
        }
        try {
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode choice = root.path("choices").path(0);
            String content = choice.path("message").path("content").asText(choice.path("text").asText(""));
            if (content == null || content.isBlank()) {
                return ResultadoQwen.fallo("QWEN_INVALID_RESPONSE", "Qwen no devolvio contenido usable", null, response.statusCode());
            }
            return new ResultadoQwen(true, "READY_FOR_REVIEW", content.trim(), null, response.statusCode());
        } catch (IOException error) {
            return ResultadoQwen.fallo("QWEN_INVALID_RESPONSE", "Qwen devolvio JSON invalido", error.getClass().getSimpleName(), response.statusCode());
        }
    }

    private static String limitarUtf8(String texto, int maxBytes) {
        String valor = texto == null ? "" : texto;
        byte[] bytes = valor.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= maxBytes) {
            return valor;
        }
        int fin = valor.length();
        while (fin > 0 && valor.substring(0, fin).getBytes(StandardCharsets.UTF_8).length > maxBytes) {
            fin--;
        }
        return valor.substring(0, fin);
    }

    public record ResultadoQwen(boolean ok, String sourceStatus, String text, String error, Integer statusCode) {
        static ResultadoQwen fallo(String sourceStatus, String text, String error, Integer statusCode) {
            return new ResultadoQwen(false, sourceStatus, text, error, statusCode);
        }
    }
}
