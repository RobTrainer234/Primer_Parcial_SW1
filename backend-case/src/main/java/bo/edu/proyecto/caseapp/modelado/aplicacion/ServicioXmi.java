package bo.edu.proyecto.caseapp.modelado.aplicacion;

import bo.edu.proyecto.caseapp.compartido.dominio.ReglaNegocioException;
import bo.edu.proyecto.caseapp.modelado.dominio.Cardinalidad;
import bo.edu.proyecto.caseapp.modelado.dominio.EntidadModelo;
import bo.edu.proyecto.caseapp.modelado.dominio.ModeloConceptual;
import bo.edu.proyecto.caseapp.modelado.dominio.RelacionModelo;
import bo.edu.proyecto.caseapp.modelado.dominio.TipoDato;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Service
public class ServicioXmi {
    private static final String ADVERTENCIA_NO_LOSSLESS = "XMI parcial academico: no es lossless; solo se conserva modelo, clases/entidades, atributos y relaciones simples soportadas.";

    private final ServicioModelado servicioModelado;
    private final Map<String, VistaPreviaXmi> vistasPrevias = new ConcurrentHashMap<>();

    public ServicioXmi(ServicioModelado servicioModelado) {
        this.servicioModelado = servicioModelado;
    }

    @Transactional(readOnly = true)
    public ExportacionXmi exportar(Long projectId, Long modelId) {
        ModeloConceptual modelo = obtenerModeloDelProyecto(projectId, modelId);
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<xmi:XMI xmlns:xmi=\"http://www.omg.org/spec/XMI/20131001\" xmlns:case=\"https://proyecto.edu.bo/caseapp/xmi-parcial\" exporter=\"caseapp-demo\" exporterVersion=\"phase-4\">\n");
        xml.append("  <case:model id=\"model-").append(modelo.getId()).append("\" name=\"").append(escapar(modelo.getNombre())).append("\" projectId=\"").append(projectId).append("\" semantic=\"UML-CLASES-MINIMO\">\n");
        for (EntidadModelo entidad : modelo.getEntidades()) {
            xml.append("    <case:entity id=\"entity-").append(entidad.getId()).append("\" name=\"").append(escapar(entidad.getNombre())).append("\" x=\"").append(entidad.getPosicionX()).append("\" y=\"").append(entidad.getPosicionY()).append("\">\n");
            entidad.getAtributos().forEach(atributo -> xml.append("      <case:attribute name=\"")
                    .append(escapar(atributo.getNombre()))
                    .append("\" type=\"").append(atributo.getTipoDato().name())
                    .append("\" primaryKey=\"").append(atributo.isClavePrimaria())
                    .append("\" required=\"").append(atributo.isObligatorio())
                    .append("\" unique=\"").append(atributo.isValorUnico())
                    .append("\"/>\n"));
            xml.append("    </case:entity>\n");
        }
        for (RelacionModelo relacion : modelo.getRelaciones()) {
            xml.append("    <case:relation name=\"").append(escapar(relacion.getNombre()))
                    .append("\" source=\"").append(escapar(relacion.getEntidadOrigen().getNombre()))
                    .append("\" target=\"").append(escapar(relacion.getEntidadDestino().getNombre()))
                    .append("\" sourceCardinality=\"").append(relacion.getCardinalidadOrigen().name())
                    .append("\" targetCardinality=\"").append(relacion.getCardinalidadDestino().name())
                    .append("\"/>\n");
        }
        xml.append("  </case:model>\n");
        xml.append("</xmi:XMI>\n");
        return new ExportacionXmi(modelo.getId(), modelo.getNombre(), "caseapp-xmi-parcial", false, ADVERTENCIA_NO_LOSSLESS, xml.toString());
    }

    @Transactional(readOnly = true)
    public VistaPreviaXmi previsualizar(Long projectId, Long modelId, String xmi) {
        obtenerModeloDelProyecto(projectId, modelId);
        ModeloImportado modelo = parsear(xmi);
        String token = UUID.randomUUID().toString();
        VistaPreviaXmi vista = new VistaPreviaXmi(
                token,
                modelId,
                false,
                ADVERTENCIA_NO_LOSSLESS,
                modelo.advertencias(),
                modelo.entidades(),
                modelo.relaciones(),
                Instant.now()
        );
        vistasPrevias.put(token, vista);
        return vista;
    }

    @Transactional
    public ConfirmacionXmi confirmar(Long projectId, Long modelId, String previewToken, boolean confirmPartialImport) {
        if (!confirmPartialImport) {
            throw new ReglaNegocioException("confirmPartialImport=true es requerido porque la importacion XMI no es lossless");
        }
        obtenerModeloDelProyecto(projectId, modelId);
        VistaPreviaXmi vista = vistasPrevias.get(previewToken);
        if (vista == null || !vista.modelId().equals(modelId)) {
            throw new ReglaNegocioException("Vista previa XMI no encontrada o no corresponde al modelo");
        }
        Map<String, EntidadModelo> entidadesPorNombre = new LinkedHashMap<>();
        for (EntidadModelo existente : servicioModelado.obtenerModelo(modelId).getEntidades()) {
            entidadesPorNombre.put(existente.getNombre().toLowerCase(), existente);
        }
        int entidadesCreadas = 0;
        int atributosCreados = 0;
        int relacionesCreadas = 0;
        for (EntidadXmi entidad : vista.entities()) {
            EntidadModelo destino = entidadesPorNombre.get(entidad.nombre().toLowerCase());
            if (destino == null) {
                destino = servicioModelado.crearEntidad(modelId, entidad.nombre(), entidad.posicionX(), entidad.posicionY());
                entidadesPorNombre.put(entidad.nombre().toLowerCase(), destino);
                entidadesCreadas++;
            }
            List<String> atributosExistentes = destino.getAtributos().stream().map(a -> a.getNombre().toLowerCase()).toList();
            for (AtributoXmi atributo : entidad.atributos()) {
                if (!atributosExistentes.contains(atributo.nombre().toLowerCase())) {
                    servicioModelado.crearAtributo(destino.getId(), atributo.nombre(), atributo.tipoDato(), atributo.clavePrimaria(), atributo.obligatorio(), atributo.valorUnico());
                    atributosCreados++;
                }
            }
        }
        for (RelacionXmi relacion : vista.relations()) {
            EntidadModelo origen = entidadesPorNombre.get(relacion.origen().toLowerCase());
            EntidadModelo destino = entidadesPorNombre.get(relacion.destino().toLowerCase());
            if (origen != null && destino != null) {
                servicioModelado.crearRelacion(modelId, origen.getId(), destino.getId(), relacion.nombre(), relacion.cardinalidadOrigen(), relacion.cardinalidadDestino());
                relacionesCreadas++;
            }
        }
        vistasPrevias.remove(previewToken);
        return new ConfirmacionXmi(modelId, true, entidadesCreadas, atributosCreados, relacionesCreadas, ADVERTENCIA_NO_LOSSLESS, vista.warnings());
    }

    private ModeloConceptual obtenerModeloDelProyecto(Long projectId, Long modelId) {
        ModeloConceptual modelo = servicioModelado.obtenerModelo(modelId);
        if (!modelo.getProyecto().getId().equals(projectId)) {
            throw new ReglaNegocioException("El modelo no pertenece al proyecto indicado");
        }
        return modelo;
    }

    private ModeloImportado parsear(String xmi) {
        if (xmi == null || xmi.isBlank()) {
            throw new ReglaNegocioException("El contenido XMI es requerido");
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            Document document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xmi.getBytes(StandardCharsets.UTF_8)));
            List<String> warnings = new ArrayList<>();
            warnings.add(ADVERTENCIA_NO_LOSSLESS);
            List<EntidadXmi> entidades = new ArrayList<>();
            NodeList nodos = document.getElementsByTagName("*");
            for (int i = 0; i < nodos.getLength(); i++) {
                Element elemento = (Element) nodos.item(i);
                String local = localName(elemento);
                String tipoXmi = atributo(elemento, "xmi:type");
                boolean esEntidad = local.equals("entity") || local.equals("class") || (local.equals("packagedElement") && tipoXmi.toLowerCase().contains("class"));
                if (!esEntidad) {
                    continue;
                }
                String nombre = primeroNoVacio(atributo(elemento, "name"), atributo(elemento, "nombre"));
                if (nombre.isBlank()) {
                    warnings.add("Se omitio una clase/entidad sin nombre.");
                    continue;
                }
                List<AtributoXmi> atributos = new ArrayList<>();
                NodeList hijos = elemento.getChildNodes();
                for (int h = 0; h < hijos.getLength(); h++) {
                    Node hijo = hijos.item(h);
                    if (hijo instanceof Element child) {
                        String childLocal = localName(child);
                        if (childLocal.equals("attribute") || childLocal.equals("ownedAttribute") || childLocal.equals("property")) {
                            String nombreAtributo = primeroNoVacio(atributo(child, "name"), atributo(child, "nombre"));
                            if (!nombreAtributo.isBlank()) {
                                atributos.add(new AtributoXmi(nombreAtributo, tipoDatoSeguro(atributo(child, "type"), warnings), bool(child, "primaryKey"), bool(child, "required"), bool(child, "unique")));
                            }
                        }
                    }
                }
                entidades.add(new EntidadXmi(nombre, enteroSeguro(atributo(elemento, "x")), enteroSeguro(atributo(elemento, "y")), atributos));
            }
            List<RelacionXmi> relaciones = new ArrayList<>();
            for (int i = 0; i < nodos.getLength(); i++) {
                Element elemento = (Element) nodos.item(i);
                String local = localName(elemento);
                if (local.equals("relation") || local.equals("association")) {
                    String origen = primeroNoVacio(atributo(elemento, "source"), atributo(elemento, "origen"));
                    String destino = primeroNoVacio(atributo(elemento, "target"), atributo(elemento, "destino"));
                    if (origen.isBlank() || destino.isBlank()) {
                        warnings.add("Relacion omitida porque no declara source/target por nombre soportado.");
                    } else {
                        relaciones.add(new RelacionXmi(primeroNoVacio(atributo(elemento, "name"), origen + "_" + destino), origen, destino, cardinalidad(atributo(elemento, "sourceCardinality")), cardinalidad(atributo(elemento, "targetCardinality"))));
                    }
                }
            }
            if (entidades.isEmpty()) {
                warnings.add("No se detectaron clases/entidades soportadas en el XMI recibido.");
            }
            warnings.add("Elementos UML avanzados, operaciones, estereotipos, diagramas visuales y namespaces externos se reportan como no soportados en esta fase.");
            return new ModeloImportado(entidades, relaciones, warnings);
        } catch (ReglaNegocioException error) {
            throw error;
        } catch (Exception error) {
            throw new ReglaNegocioException("No se pudo leer el XMI: " + error.getMessage());
        }
    }

    private static String escapar(String valor) {
        return valor == null ? "" : valor.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String localName(Element elemento) {
        return elemento.getLocalName() == null ? elemento.getTagName().replaceFirst("^.*:", "") : elemento.getLocalName();
    }

    private static String atributo(Element elemento, String nombre) {
        if (elemento.hasAttribute(nombre)) {
            return elemento.getAttribute(nombre).trim();
        }
        return "";
    }

    private static String primeroNoVacio(String primero, String segundo) {
        return primero == null || primero.isBlank() ? (segundo == null ? "" : segundo) : primero;
    }

    private static Integer enteroSeguro(String valor) {
        try {
            return valor == null || valor.isBlank() ? 0 : Integer.parseInt(valor);
        } catch (NumberFormatException error) {
            return 0;
        }
    }

    private static boolean bool(Element elemento, String nombre) {
        return "true".equalsIgnoreCase(atributo(elemento, nombre));
    }

    private static TipoDato tipoDatoSeguro(String valor, List<String> warnings) {
        if (valor == null || valor.isBlank()) {
            return TipoDato.TEXTO;
        }
        try {
            return TipoDato.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException error) {
            warnings.add("Tipo de dato no soportado '" + valor + "'; se importara como TEXTO.");
            return TipoDato.TEXTO;
        }
    }

    private static Cardinalidad cardinalidad(String valor) {
        if (valor == null || valor.isBlank()) {
            return Cardinalidad.UNO;
        }
        try {
            return Cardinalidad.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException error) {
            return valor.contains("*") || valor.toLowerCase().contains("many") ? Cardinalidad.MUCHOS : Cardinalidad.UNO;
        }
    }

    private record ModeloImportado(List<EntidadXmi> entidades, List<RelacionXmi> relaciones, List<String> advertencias) {}

    public record ExportacionXmi(Long modelId, String modelName, String format, boolean lossless, String limitation, String xmi) {}
    public record VistaPreviaXmi(String previewToken, Long modelId, boolean lossless, String limitation, List<String> warnings, List<EntidadXmi> entities, List<RelacionXmi> relations, Instant createdAt) {}
    public record ConfirmacionXmi(Long modelId, boolean applied, int entitiesCreated, int attributesCreated, int relationsCreated, String limitation, List<String> warnings) {}
    public record EntidadXmi(String nombre, Integer posicionX, Integer posicionY, List<AtributoXmi> atributos) {}
    public record AtributoXmi(String nombre, TipoDato tipoDato, boolean clavePrimaria, boolean obligatorio, boolean valorUnico) {}
    public record RelacionXmi(String nombre, String origen, String destino, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {}
}
