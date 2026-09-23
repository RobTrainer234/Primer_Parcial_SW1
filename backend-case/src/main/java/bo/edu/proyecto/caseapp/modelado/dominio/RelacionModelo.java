package bo.edu.proyecto.caseapp.modelado.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "relaciones_modelo")
public class RelacionModelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modelo_id", nullable = false)
    private ModeloConceptual modelo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entidad_origen_id", nullable = false)
    private EntidadModelo entidadOrigen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entidad_destino_id", nullable = false)
    private EntidadModelo entidadDestino;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 120)
    private String verbo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoRelacion tipo = TipoRelacion.ASOCIACION;

    @Enumerated(EnumType.STRING)
    @Column(name = "cardinalidad_origen", nullable = false, length = 20)
    private Cardinalidad cardinalidadOrigen;

    @Enumerated(EnumType.STRING)
    @Column(name = "cardinalidad_destino", nullable = false, length = 20)
    private Cardinalidad cardinalidadDestino;

    protected RelacionModelo() {
    }

    public RelacionModelo(ModeloConceptual modelo, EntidadModelo entidadOrigen, EntidadModelo entidadDestino, String nombre, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        this(modelo, entidadOrigen, entidadDestino, nombre, null, cardinalidadOrigen, cardinalidadDestino);
    }

    public RelacionModelo(ModeloConceptual modelo, EntidadModelo entidadOrigen, EntidadModelo entidadDestino, String nombre, String verbo, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        this(modelo, entidadOrigen, entidadDestino, nombre, verbo, TipoRelacion.ASOCIACION, cardinalidadOrigen, cardinalidadDestino);
    }

    public RelacionModelo(ModeloConceptual modelo, EntidadModelo entidadOrigen, EntidadModelo entidadDestino, String nombre, String verbo, TipoRelacion tipo, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        this.modelo = modelo;
        this.entidadOrigen = entidadOrigen;
        this.entidadDestino = entidadDestino;
        this.nombre = nombre;
        this.verbo = verbo;
        this.tipo = tipo != null ? tipo : TipoRelacion.ASOCIACION;
        this.cardinalidadOrigen = cardinalidadOrigen;
        this.cardinalidadDestino = cardinalidadDestino;
    }

    public void actualizar(EntidadModelo entidadOrigen, EntidadModelo entidadDestino, String nombre, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        actualizar(entidadOrigen, entidadDestino, nombre, null, cardinalidadOrigen, cardinalidadDestino);
    }

    public void actualizar(EntidadModelo entidadOrigen, EntidadModelo entidadDestino, String nombre, String verbo, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        actualizar(entidadOrigen, entidadDestino, nombre, verbo, TipoRelacion.ASOCIACION, cardinalidadOrigen, cardinalidadDestino);
    }

    public void actualizar(EntidadModelo entidadOrigen, EntidadModelo entidadDestino, String nombre, String verbo, TipoRelacion tipo, Cardinalidad cardinalidadOrigen, Cardinalidad cardinalidadDestino) {
        this.entidadOrigen = entidadOrigen;
        this.entidadDestino = entidadDestino;
        this.nombre = nombre;
        this.verbo = verbo;
        this.tipo = tipo != null ? tipo : TipoRelacion.ASOCIACION;
        this.cardinalidadOrigen = cardinalidadOrigen;
        this.cardinalidadDestino = cardinalidadDestino;
    }

    public Long getId() { return id; }
    public ModeloConceptual getModelo() { return modelo; }
    public EntidadModelo getEntidadOrigen() { return entidadOrigen; }
    public EntidadModelo getEntidadDestino() { return entidadDestino; }
    public String getNombre() { return nombre; }
    public String getVerbo() { return verbo; }
    public TipoRelacion getTipo() { return tipo; }
    public Cardinalidad getCardinalidadOrigen() { return cardinalidadOrigen; }
    public Cardinalidad getCardinalidadDestino() { return cardinalidadDestino; }
}
