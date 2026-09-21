package bo.edu.proyecto.caseapp.generacion.aplicacion;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class NormalizadorNombres {

    private static final Set<String> PALABRAS_RESERVADAS = Stream.of(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"
    ).collect(Collectors.toSet());

    public String clase(String valor) {
        String limpio = limpiar(valor);
        if (limpio.isBlank()) {
            return "Entidad";
        }
        String[] partes = limpio.split("_");
        StringBuilder resultado = new StringBuilder();
        for (String parte : partes) {
            if (!parte.isBlank()) {
                resultado.append(parte.substring(0, 1).toUpperCase(Locale.ROOT));
                if (parte.length() > 1) {
                    resultado.append(parte.substring(1));
                }
            }
        }
        String nombre = resultado.toString();
        if (Character.isDigit(nombre.charAt(0))) {
            nombre = "Entidad" + nombre;
        }
        return nombre;
    }

    public String variable(String valor) {
        String clase = clase(valor);
        String nombre = clase.substring(0, 1).toLowerCase(Locale.ROOT) + clase.substring(1);
        if (PALABRAS_RESERVADAS.contains(nombre)) {
            return nombre + "Valor";
        }
        return nombre;
    }

    public String metodo(String valor) {
        return clase(valor);
    }

    public String recurso(String valor) {
        String limpio = limpiar(valor).replace('_', '-').toLowerCase(Locale.ROOT);
        return limpio.isBlank() ? "recursos" : limpio + "s";
    }

    public String paquete(String valor) {
        String limpio = limpiar(valor).toLowerCase(Locale.ROOT).replace('_', '.');
        if (limpio.isBlank()) {
            return "bo.edu.generado.aplicacion";
        }
        return "bo.edu.generado." + limpio;
    }

    private String limpiar(String valor) {
        if (valor == null) {
            return "";
        }
        String sinAcentos = Normalizer.normalize(valor, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String limpio = sinAcentos.replaceAll("[^A-Za-z0-9]+", "_").replaceAll("_+", "_");
        limpio = limpio.replaceAll("^_", "").replaceAll("_$", "");
        return limpio;
    }
}
