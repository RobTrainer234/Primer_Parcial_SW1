package bo.edu.proyecto.caseapp.modelado.dominio;

import bo.edu.proyecto.caseapp.proyectos.dominio.Proyecto;
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
@Table(name = "modelos_conceptuales")
public class ModeloConceptual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false)
    private Integer version = 1;

    @OneToMany(mappedBy = "modelo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntidadModelo> entidades = new ArrayList<>();

    @OneToMany(mappedBy = "modelo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RelacionModelo> relaciones = new ArrayList<>();

    protected ModeloConceptual() {
    }

    public ModeloConceptual(Proyecto proyecto, String nombre) {
        this.proyecto = proyecto;
        this.nombre = nombre;
        this.version = 1;
    }

    public void actualizarNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public Proyecto getProyecto() { return proyecto; }
    public String getNombre() { return nombre; }
    public Integer getVersion() { return version; }
    public List<EntidadModelo> getEntidades() { return entidades; }
    public List<RelacionModelo> getRelaciones() { return relaciones; }
}
