package bo.edu.proyecto.caseapp.proyectos.aplicacion;

import static org.assertj.core.api.Assertions.assertThat;

import bo.edu.proyecto.caseapp.configuracion.PropiedadesAiPropuestas;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ClienteQwenPropuestasTest {
    private HttpServer server;

    @AfterEach
    void detenerServidor() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void extraeContenidoOpenAiCompatible() throws Exception {
        iniciarServidor(200, "{\"choices\":[{\"message\":{\"content\":\"Crear entidad Turno.\"}}]}");

        ClienteQwenPropuestas cliente = new ClienteQwenPropuestas(new ObjectMapper());
        ClienteQwenPropuestas.ResultadoQwen resultado = cliente.solicitar("Agregar turnos", propiedades());

        assertThat(resultado.ok()).isTrue();
        assertThat(resultado.sourceStatus()).isEqualTo("READY_FOR_REVIEW");
        assertThat(resultado.text()).isEqualTo("Crear entidad Turno.");
    }

    @Test
    void controlaRespuestaNoExitosa() throws Exception {
        iniciarServidor(503, "servicio no disponible");

        ClienteQwenPropuestas cliente = new ClienteQwenPropuestas(new ObjectMapper());
        ClienteQwenPropuestas.ResultadoQwen resultado = cliente.solicitar("Agregar turnos", propiedades());

        assertThat(resultado.ok()).isFalse();
        assertThat(resultado.sourceStatus()).isEqualTo("QWEN_HTTP_ERROR");
        assertThat(resultado.statusCode()).isEqualTo(503);
    }

    @Test
    void controlaJsonInvalido() throws Exception {
        iniciarServidor(200, "no-json");

        ClienteQwenPropuestas cliente = new ClienteQwenPropuestas(new ObjectMapper());
        ClienteQwenPropuestas.ResultadoQwen resultado = cliente.solicitar("Agregar turnos", propiedades());

        assertThat(resultado.ok()).isFalse();
        assertThat(resultado.sourceStatus()).isEqualTo("QWEN_INVALID_RESPONSE");
    }

    private void iniciarServidor(int estado, String respuesta) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/chat/completions", exchange -> {
            byte[] body = respuesta.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(estado, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
    }

    private PropiedadesAiPropuestas.Qwen propiedades() {
        PropiedadesAiPropuestas.Qwen qwen = new PropiedadesAiPropuestas.Qwen();
        qwen.setEndpoint("http://127.0.0.1:" + server.getAddress().getPort() + "/chat/completions");
        qwen.setConnectTimeoutMs(500);
        qwen.setReadTimeoutMs(1000);
        qwen.setMaxResponseBytes(32768);
        return qwen;
    }
}
