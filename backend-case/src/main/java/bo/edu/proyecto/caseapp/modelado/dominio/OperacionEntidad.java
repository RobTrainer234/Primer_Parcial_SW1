package bo.edu.proyecto.caseapp.modelado.dominio;

import bo.edu.proyecto.caseapp.compartido.dominio.ReglaNegocioException;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "operaciones_entidad")
public class OperacionEntidad {
    public enum Visibilidad { PUBLICA, PROTEGIDA, PRIVADA, PAQUETE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entidad_id", nullable = false)
    private EntidadModelo entidad;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "tipo_retorno", nullable = false, length = 120)
    private String tipoRetorno;

    @Column(nullable = false, length = 240)
    private String firma;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Visibilidad visibilidad;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    protected OperacionEntidad() {
    }

    public OperacionEntidad(EntidadModelo entidad, String nombre, String tipoRetorno, String firma, Visibilidad visibilidad) {
        this.entidad = entidad;
        actualizar(nombre, tipoRetorno, firma, visibilidad);
    }

    @PrePersist
    void antesDeCrear() {
        Instant ahora = Instant.now();
        creadoEn = ahora;
        actualizadoEn = ahora;
    }

    @PreUpdate
    void antesDeActualizar() {
        actualizadoEn = Instant.now();
    }

    public void actualizar(String nombre, String tipoRetorno, String firma, Visibilidad visibilidad) {
        this.nombre = texto(nombre, "El nombre de la operacion es obligatorio", 120);
        this.tipoRetorno = texto(tipoRetorno, "El tipo de retorno de la operacion es obligatorio", 120);
        this.firma = texto(firma, "La firma de la operacion es obligatoria", 240);
        this.visibilidad = visibilidad == null ? Visibilidad.PUBLICA : visibilidad;
    }

    public String representacionUml() {
        return prefijoVisibilidad() + nombre + firma + " : " + tipoRetorno;
    }

    private String prefijoVisibilidad() {
        return switch (visibilidad) {
            case PUBLICA -> "+";
            case PROTEGIDA -> "#";
            case PRIVADA -> "-";
            case PAQUETE -> "~";
        };
    }

    private static String texto(String valor, String mensaje, int maximo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException(mensaje);
        }
        String limpio = valor.trim();
        if (limpio.length() > maximo) {
            throw new ReglaNegocioException("El texto supera " + maximo + " caracteres");
        }
        return limpio;
    }

    public Long getId() { return id; }
    public EntidadModelo getEntidad() { return entidad; }
    public String getNombre() { return nombre; }
    public String getTipoRetorno() { return tipoRetorno; }
    public String getFirma() { return firma; }
    public Visibilidad getVisibilidad() { return visibilidad; }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }
}
