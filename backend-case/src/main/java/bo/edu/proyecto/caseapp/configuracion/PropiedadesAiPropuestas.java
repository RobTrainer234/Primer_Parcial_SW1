package bo.edu.proyecto.caseapp.configuracion;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "caseapp.ai.text-proposals")
public class PropiedadesAiPropuestas {
    private boolean enabled = false;
    private String provider = "deterministic";
    private final Qwen qwen = new Qwen();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Qwen getQwen() {
        return qwen;
    }

    public static class Qwen {
        private String endpoint = "http://127.0.0.1:12434/engines/v1/chat/completions";
        private String model = "hf.co/ggml-org/Qwen3.5-0.8B-GGUF:Q4_0";
        private int connectTimeoutMs = 2000;
        private int readTimeoutMs = 120000;
        private int maxRequestBytes = 16000000;
        private int maxResponseBytes = 32768;
        private int maxContextBytes = 8192;
        private int maxCompletionTokens = 768;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public int getConnectTimeoutMs() {
            return connectTimeoutMs;
        }

        public void setConnectTimeoutMs(int connectTimeoutMs) {
            this.connectTimeoutMs = connectTimeoutMs;
        }

        public int getReadTimeoutMs() {
            return readTimeoutMs;
        }

        public void setReadTimeoutMs(int readTimeoutMs) {
            this.readTimeoutMs = readTimeoutMs;
        }

        public int getMaxRequestBytes() {
            return maxRequestBytes;
        }

        public void setMaxRequestBytes(int maxRequestBytes) {
            this.maxRequestBytes = maxRequestBytes;
        }

        public int getMaxResponseBytes() {
            return maxResponseBytes;
        }

        public void setMaxResponseBytes(int maxResponseBytes) {
            this.maxResponseBytes = maxResponseBytes;
        }

        public int getMaxContextBytes() {
            return maxContextBytes;
        }

        public void setMaxContextBytes(int maxContextBytes) {
            this.maxContextBytes = maxContextBytes;
        }

        public int getMaxCompletionTokens() {
            return maxCompletionTokens;
        }

        public void setMaxCompletionTokens(int maxCompletionTokens) {
            this.maxCompletionTokens = maxCompletionTokens;
        }
    }
}
