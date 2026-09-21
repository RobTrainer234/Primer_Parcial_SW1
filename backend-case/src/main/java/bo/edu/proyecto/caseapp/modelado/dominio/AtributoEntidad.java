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
@Table(name = "atributos_entidad")
public class AtributoEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entidad_id", nullable = false)
    private EntidadModelo entidad;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_dato", nullable = false, length = 40)
    private TipoDato tipoDato;

    @Column(name = "clave_primaria", nullable = false)
    private boolean clavePrimaria;

    @Column(nullable = false)
    private boolean obligatorio;

    @Column(name = "valor_unico", nullable = false)
    private boolean valorUnico;

    protected AtributoEntidad() {
    }

    public AtributoEntidad(EntidadModelo entidad, String nombre, TipoDato tipoDato, boolean clavePrimaria, boolean obligatorio, boolean valorUnico) {
        this.entidad = entidad;
        this.nombre = nombre;
        this.tipoDato = tipoDato;
        this.clavePrimaria = clavePrimaria;
        this.obligatorio = obligatorio;
        this.valorUnico = valorUnico;
    }

    public void actualizar(String nombre, TipoDato tipoDato, boolean clavePrimaria, boolean obligatorio, boolean valorUnico) {
        this.nombre = nombre;
        this.tipoDato = tipoDato;
        this.clavePrimaria = clavePrimaria;
        this.obligatorio = obligatorio;
        this.valorUnico = valorUnico;
    }

    public Long getId() { return id; }
    public EntidadModelo getEntidad() { return entidad; }
    public String getNombre() { return nombre; }
    public TipoDato getTipoDato() { return tipoDato; }
    public boolean isClavePrimaria() { return clavePrimaria; }
    public boolean isObligatorio() { return obligatorio; }
    public boolean isValorUnico() { return valorUnico; }
}
