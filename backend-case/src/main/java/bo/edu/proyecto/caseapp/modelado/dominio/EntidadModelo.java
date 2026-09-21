package bo.edu.proyecto.caseapp.modelado.dominio;

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

    protected EntidadModelo() {
    }

    public EntidadModelo(ModeloConceptual modelo, String nombre, Integer posicionX, Integer posicionY) {
        this.modelo = modelo;
        this.nombre = nombre;
        this.posicionX = posicionX == null ? 0 : posicionX;
        this.posicionY = posicionY == null ? 0 : posicionY;
    }

    public void actualizar(String nombre, Integer posicionX, Integer posicionY) {
        this.nombre = nombre;
        this.posicionX = posicionX == null ? this.posicionX : posicionX;
        this.posicionY = posicionY == null ? this.posicionY : posicionY;
    }

    public Long getId() { return id; }
    public ModeloConceptual getModelo() { return modelo; }
    public String getNombre() { return nombre; }
    public Integer getPosicionX() { return posicionX; }
    public Integer getPosicionY() { return posicionY; }
    public List<AtributoEntidad> getAtributos() { return atributos; }
}
