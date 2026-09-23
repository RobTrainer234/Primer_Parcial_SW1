package bo.edu.proyecto.caseapp.modelado.dominio;

import bo.edu.proyecto.caseapp.compartido.dominio.ReglaNegocioException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entidades_modelo")
public class EntidadModelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modelo_id", nullable = false)
    private ModeloConceptual modelo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "posicion_x", nullable = false)
    private Integer posicionX;

    @Column(name = "posicion_y", nullable = false)
    private Integer posicionY;

    @OneToMany(mappedBy = "entidad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AtributoEntidad> atributos = new ArrayList<>();

    @OneToMany(mappedBy = "entidad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OperacionEntidad> operaciones = new ArrayList<>();

    protected EntidadModelo() {
    }

    public EntidadModelo(ModeloConceptual modelo, String nombre, Integer posicionX, Integer posicionY) {
        this.modelo = modelo;
        this.nombre = nombreValido(nombre);
        this.posicionX = posicionX == null ? 0 : posicionX;
        this.posicionY = posicionY == null ? 0 : posicionY;
    }

    public void actualizar(String nombre, Integer posicionX, Integer posicionY) {
        this.nombre = nombreValido(nombre);
        this.posicionX = posicionX == null ? this.posicionX : posicionX;
        this.posicionY = posicionY == null ? this.posicionY : posicionY;
    }

    private static String nombreValido(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("El nombre de la entidad es obligatorio");
        }
        String limpio = valor.trim();
        if (limpio.length() > 120) {
            throw new ReglaNegocioException("El nombre de la entidad supera 120 caracteres");
        }
        return limpio;
    }

    public Long getId() { return id; }
    public ModeloConceptual getModelo() { return modelo; }
    public String getNombre() { return nombre; }
    public Integer getPosicionX() { return posicionX; }
    public Integer getPosicionY() { return posicionY; }
    public List<AtributoEntidad> getAtributos() { return atributos; }
    public List<OperacionEntidad> getOperaciones() { return operaciones; }
}
